package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.BarcodeProcessingStrategy
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.currentTimeMillis
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.calculateAreaRatio
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.convertToViewCoordinates
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.getAllowedAVMetadataObjectTypes
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.toDomainFormat
import io.github.ismoy.imagepickerkmp.scanner.utils.scanner
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

/**
 * Platform-specific image analyzer for iOS (AVFoundation).
 *
 * After SRP refactor this class is only responsible for:
 *  1. Attaching AVCaptureMetadataOutput to the capture session
 *  2. Converting AVMetadataMachineReadableCodeObject to domain models
 *  3. Delegating all business rules to [BarcodeProcessingStrategy]
 *  4. Emitting [ScannerEvent]s
 */
@OptIn(ExperimentalForeignApi::class)
internal class IOSBarcodeAnalyzer internal constructor(
    private val config: ScannerCameraConfig,
    private val eventManager: ScannerEventManager,
    private val stateManager: DefaultScannerCameraStateManager,
    private val soundManager: IOSScannerSoundManager,
    private val getPreviewLayer: () -> AVCaptureVideoPreviewLayer?,
    private val getIsScanning: () -> Boolean
) {
    private val logger = LoggerFactory.getLogger()
    private var metadataDelegate: ScannerMetadataDelegate? = null

    /** Business-rule engine — no scanning logic lives in this class. */
    private val processingStrategy = BarcodeProcessingStrategy(
        behaviorConfig = config.behavior,
        advancedConfig = config.advanced
    )

    // ── Session attachment ────────────────────────────────────────────────────

    fun attachToSession(session: AVCaptureSession) {
        val metadataOutput = AVCaptureMetadataOutput()
        if (session.canAddOutput(metadataOutput)) {
            session.addOutput(metadataOutput)
            metadataOutput.metadataObjectTypes = config.behavior.getAllowedAVMetadataObjectTypes()
            metadataDelegate = ScannerMetadataDelegate()
            metadataOutput.setMetadataObjectsDelegate(metadataDelegate, dispatch_get_main_queue())
        }
    }

    fun clearState() {
        processingStrategy.reset()
        metadataDelegate = null
    }

    // ── AVCaptureMetadataOutputObjectsDelegate ────────────────────────────────

    private inner class ScannerMetadataDelegate :
        NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

        override fun captureOutput(
            output: AVCaptureOutput,
            didOutputMetadataObjects: List<*>,
            fromConnection: AVCaptureConnection
        ) {
            if (!getIsScanning()) return
            val currentTime = currentTimeMillis()

            if (didOutputMetadataObjects.isEmpty()) {
                processingStrategy.updateDistance(CameraPositionDistance.TOO_FAR)
                eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
                eventManager.emitEvent(ScannerEvent.BarcodesDetected(emptyList()))
                return
            }

            val previewLayer = getPreviewLayer() ?: return
            val domainBarcodes = mutableListOf<BarcodeData>()

            for (metadata in didOutputMetadataObjects) {
                val readable = metadata as? AVMetadataMachineReadableCodeObject ?: continue
                val barcodeData = readable.toDomainModel(previewLayer) ?: continue
                if (processingStrategy.isValidFormat(barcodeData.format, barcodeData.rawValue)) {
                    domainBarcodes.add(barcodeData)
                    updateDistanceFromReadable(readable)
                }
            }

            val distinctBarcodes = domainBarcodes.distinctBy { it.rawValue }
            processingStrategy.retainVisibleCodes(distinctBarcodes.map { it.rawValue }.toSet())
            eventManager.emitEvent(ScannerEvent.BarcodesDetected(distinctBarcodes))
            processValidBarcodes(distinctBarcodes, currentTime)
        }
    }

    // ── Domain model conversion ───────────────────────────────────────────────

    private fun AVMetadataMachineReadableCodeObject.toDomainModel(
        previewLayer: AVCaptureVideoPreviewLayer
    ): BarcodeData? {
        val code = stringValue ?: return null
        val bounds = convertToViewCoordinates(previewLayer) ?: return null
        val format = type?.toDomainFormat() ?: BarcodeFormat.UNKNOWN

        return bounds.useContents {
            val rect = ScannerRect(
                left = origin.x.toFloat(),
                top = origin.y.toFloat(),
                right = (origin.x + size.width).toFloat(),
                bottom = (origin.y + size.height).toFloat(),
                sourceWidth = previewLayer.bounds.useContents { size.width }.toFloat(),
                sourceHeight = previewLayer.bounds.useContents { size.height }.toFloat(),
                rotation = 0
            )
            BarcodeData(rawValue = code, format = format, boundingBox = rect, cornerPoints = null)
        }
    }

    // ── Barcode processing (delegates rules to strategy) ─────────────────────

    private fun updateDistanceFromReadable(readable: AVMetadataMachineReadableCodeObject) {
        val ratio = readable.calculateAreaRatio()
        val distance = processingStrategy.calculateDistance(ratio)
        processingStrategy.updateDistance(distance)
        eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
    }

    private fun processValidBarcodes(barcodes: List<BarcodeData>, currentTime: Long) {
        var codeEmitted = false
        var hasValidBarcode = false
        for (barcode in barcodes) {
            hasValidBarcode = true
            if (config.advanced.batchMode || !codeEmitted) {
                if (tryEmitBarcode(barcode, currentTime)) codeEmitted = true
            }
        }

        if (!hasValidBarcode) {
            processingStrategy.updateDistance(CameraPositionDistance.TOO_FAR)
            eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
        }
    }

    private fun tryEmitBarcode(barcode: BarcodeData, currentTime: Long): Boolean {
        if (!processingStrategy.shouldEmit(barcode.rawValue, currentTime)) return false
        if (processingStrategy.isPickAndPackSuppressed()) return false

        soundManager.playBeepSound()
        eventManager.emitEvent(ScannerEvent.CodeScanned(barcode.rawValue, barcode.format.name))
        stateManager.updateState(ScannerCameraState.CodeDetected(barcode.rawValue, barcode.format.name))
        logger.scanner("Code accepted (format=${barcode.format.name}, length=${barcode.rawValue.length})")
        return true
    }
}
