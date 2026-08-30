package io.github.ismoy.imagepickerkmp.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.BarcodeProcessingStrategy
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerPoint
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.scanner

/**
 * Platform-specific image analyzer for Android (CameraX + MLKit).
 *
 * After SRP refactor this class is only responsible for:
 *  1. Receiving camera frames via [ImageAnalysis.Analyzer]
 *  2. Running MLKit barcode detection
 *  3. Converting MLKit results to domain models
 *  4. Delegating all business rules to [BarcodeProcessingStrategy]
 *  5. Emitting [ScannerEvent]s
 *
 * Android-specific phantom-code guard (not needed on iOS):
 *  MLKit on Android can decode a valid-looking barcode from a motion-blurred frame
 *  when the camera is moving (AR mode).  The [BoundingBoxStabilityTracker] requires
 *  the detected bounding box to remain spatially stable across consecutive frames
 *  before the code is passed downstream.  A phantom produced by a single blurred
 *  frame will never satisfy this stability requirement and is silently dropped.
 */
internal class MLKitBarcodeAnalyzer(
    private val config: ScannerCameraConfig,
    private val eventManager: ScannerEventManager,
    private val stateManager: DefaultScannerCameraStateManager,
    private val isScannerActive: () -> Boolean,
    private val onPlaySound: () -> Unit
) : ImageAnalysis.Analyzer {

    private val logger = LoggerFactory.getLogger()
    private val mlkitScanner = BarcodeScanning.getClient(buildScannerOptions())
    private var isClosed = false

    /** Business-rule engine — no scanning logic lives in this class. */
    private val processingStrategy = BarcodeProcessingStrategy(
        behaviorConfig = config.behavior,
        advancedConfig = config.advanced
    )

    private val stabilityTracker = BarcodeBoundingBoxStabilityTracker()

    fun close() {
        if (isClosed) return
        isClosed = true
        processingStrategy.reset()
        stabilityTracker.reset()
        mlkitScanner.close()
    }

    // ── MLKit options ─────────────────────────────────────────────────────────

    private fun buildScannerOptions(): BarcodeScannerOptions {
        val builder = BarcodeScannerOptions.Builder()
        if (config.behavior.supportInvertedBarcodes) builder.enableAllPotentialBarcodes()
        if (!config.behavior.allowedFormats.contains(BarcodeFormat.ALL)) {
            val formats = config.behavior.allowedFormats.mapNotNull { it.toMLKitFormat() }
            if (formats.isNotEmpty()) {
                builder.setBarcodeFormats(formats.first(), *formats.drop(1).toIntArray())
            }
        }
        return builder.build()
    }

    private fun BarcodeFormat.toMLKitFormat(): Int? = when (this) {
        BarcodeFormat.QR_CODE    -> Barcode.FORMAT_QR_CODE
        BarcodeFormat.AZTEC      -> Barcode.FORMAT_AZTEC
        BarcodeFormat.CODE_128   -> Barcode.FORMAT_CODE_128
        BarcodeFormat.CODE_39    -> Barcode.FORMAT_CODE_39
        BarcodeFormat.CODE_93    -> Barcode.FORMAT_CODE_93
        BarcodeFormat.CODABAR    -> Barcode.FORMAT_CODABAR
        BarcodeFormat.DATA_MATRIX -> Barcode.FORMAT_DATA_MATRIX
        BarcodeFormat.EAN_13     -> Barcode.FORMAT_EAN_13
        BarcodeFormat.EAN_8      -> Barcode.FORMAT_EAN_8
        BarcodeFormat.ITF        -> Barcode.FORMAT_ITF
        BarcodeFormat.PDF_417    -> Barcode.FORMAT_PDF417
        BarcodeFormat.UPC_A      -> Barcode.FORMAT_UPC_A
        BarcodeFormat.UPC_E      -> Barcode.FORMAT_UPC_E
        else                     -> null
    }

    // ── ImageAnalysis.Analyzer ────────────────────────────────────────────────

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (isClosed || !isScannerActive()) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        mlkitScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (isClosed || !isScannerActive()) return@addOnSuccessListener

                if (barcodes.isEmpty()) {
                    processingStrategy.updateDistance(CameraPositionDistance.TOO_FAR)
                    eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
                    eventManager.emitEvent(ScannerEvent.BarcodesDetected(emptyList()))
                    return@addOnSuccessListener
                }

                val domainBarcodes = BarcodeSpatialDeduplicator.distinctPhysicalBarcodes(
                    barcodes.mapNotNull { it.toDomainModel(imageProxy) }
                        .filter { processingStrategy.isValidFormat(it.format, it.rawValue) }
                        .distinctBy { it.rawValue }
                )
                val visibleValues = domainBarcodes.map { it.rawValue }.toSet()
                processingStrategy.retainVisibleCodes(visibleValues)
                stabilityTracker.retainVisible(visibleValues)
                eventManager.emitEvent(ScannerEvent.BarcodesDetected(domainBarcodes))
                processValidBarcodes(domainBarcodes, imageProxy, System.currentTimeMillis())
            }
            .addOnFailureListener { e ->
                logger.error("MLKit", "Error processing barcode", e)
                processingStrategy.reset()
                stabilityTracker.reset()
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    // ── Domain model conversion ───────────────────────────────────────────────

    private fun Barcode.toDomainModel(imageProxy: ImageProxy): BarcodeData? {
        val rawValue = rawValue ?: return null
        val format = format.toDomainFormat()

        val rotation = imageProxy.imageInfo.rotationDegrees
        val isRotated = rotation == 90 || rotation == 270
        val srcW = if (isRotated) imageProxy.height.toFloat() else imageProxy.width.toFloat()
        val srcH = if (isRotated) imageProxy.width.toFloat() else imageProxy.height.toFloat()

        val rect = boundingBox?.let { b ->
            ScannerRect(
                left = b.left.toFloat(),
                top = b.top.toFloat(),
                right = b.right.toFloat(),
                bottom = b.bottom.toFloat(),
                sourceWidth = srcW,
                sourceHeight = srcH,
                rotation = 0
            )
        } ?: return null

        val points = cornerPoints?.map { p -> ScannerPoint(p.x.toFloat(), p.y.toFloat()) }
        return BarcodeData(rawValue, format, rect, points)
    }

    private fun Int.toDomainFormat(): BarcodeFormat =
        MLKitBarcodeFormatMapper.getDomainFormat(this)

    // ── Barcode processing (delegates rules to strategy) ─────────────────────

    private fun processValidBarcodes(
        barcodes: List<BarcodeData>,
        imageProxy: ImageProxy,
        currentTime: Long
    ) {
        var codeEmitted = false
        var hasValidBarcode = false
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            val format = barcode.format

            if (!processingStrategy.isValidFormat(format, rawValue)) continue

            hasValidBarcode = true
            updateDistanceFromBarcode(barcode, imageProxy)

            if (config.advanced.batchMode || !codeEmitted) {
                if (tryEmitBarcode(rawValue, format, barcode, imageProxy, currentTime)) {
                    codeEmitted = true
                }
            }
        }

        if (!hasValidBarcode) {
            processingStrategy.updateDistance(CameraPositionDistance.TOO_FAR)
            eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
        }
    }

    private fun updateDistanceFromBarcode(barcode: BarcodeData, imageProxy: ImageProxy) {
        barcode.boundingBox?.let { bounds ->
            // Use the rotated dimensions so the area ratio matches the coordinate
            // space of the bounding box returned by MLKit (which already accounts
            // for rotationDegrees applied via InputImage.fromMediaImage).
            val rotation = imageProxy.imageInfo.rotationDegrees
            val isRotated = rotation == 90 || rotation == 270
            val frameW = if (isRotated) imageProxy.height.toFloat() else imageProxy.width.toFloat()
            val frameH = if (isRotated) imageProxy.width.toFloat() else imageProxy.height.toFloat()
            val areaRatio = (bounds.width * bounds.height) / (frameW * frameH)
            val distance = processingStrategy.calculateDistance(areaRatio)
            processingStrategy.updateDistance(distance)
            eventManager.emitEvent(ScannerEvent.DistanceChanged(processingStrategy.getSmoothedDistance()))
        }
    }

    private fun tryEmitBarcode(
        value: String,
        format: BarcodeFormat,
        barcode: BarcodeData,
        imageProxy: ImageProxy,
        currentTime: Long
    ): Boolean {
        // ── Guard 1: bounding-box spatial stability across frames ─────────────
        // Phantoms from motion blur appear with a random bounding box position that
        // never repeats across consecutive frames. A real code stays spatially
        // stable. Reject the code until its box has been stable for STABILITY_FRAMES.
        val rect = barcode.boundingBox
        if (rect != null) {
            // Use rotated dimensions so normalised center matches the coordinate
            // space of the bounding box (MLKit already applies rotation).
            val rotation = imageProxy.imageInfo.rotationDegrees
            val isRotated = rotation == 90 || rotation == 270
            val imgW = (if (isRotated) imageProxy.height else imageProxy.width).toFloat().coerceAtLeast(1f)
            val imgH = (if (isRotated) imageProxy.width else imageProxy.height).toFloat().coerceAtLeast(1f)
            val cx = ((rect.left + rect.right) / 2f) / imgW
            val cy = ((rect.top + rect.bottom) / 2f) / imgH
            if (!stabilityTracker.isStable(value, cx, cy)) {
                logger.scanner(
                    "Stability guard: rejecting ${format.name} code (length=${value.length}) — " +
                        "bounding box not yet stable"
                )
                return false
            }
        }

        // ── Guard 2: phantom-of-last-emit check ───────────────────────────────
        // Rejects a code that looks like a motion-blur corruption of the last
        // emitted code and is still within the cooldown window.
        if (processingStrategy.isPhantomOfLastEmit(value, currentTime)) {
            logger.scanner(
                "Phantom guard: rejecting ${format.name} code (length=${value.length}) — " +
                    "similar to last emitted code within cooldown"
            )
            return false
        }

        if (!processingStrategy.shouldEmit(value, currentTime)) return false
        if (processingStrategy.isPickAndPackSuppressed()) return false

        processingStrategy.recordEmit(value, currentTime)
        onPlaySound()
        eventManager.emitEvent(ScannerEvent.CodeScanned(value, format.name))
        stateManager.updateState(ScannerCameraState.CodeDetected(value, format.name))
        logger.scanner("Code accepted (format=${format.name}, length=${value.length})")
        return true
    }
}
