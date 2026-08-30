package io.github.ismoy.imagepickerkmp.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.camera
import io.github.ismoy.imagepickerkmp.scanner.utils.scanner
import kotlinx.coroutines.flow.MutableStateFlow

internal class AndroidCameraXBinder(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val config: ScannerCameraConfig,
    private val eventManager: ScannerEventManager,
    private val stateManager: DefaultScannerCameraStateManager,
    private val soundManager: AndroidScannerSoundManager,
    private val flashManager: AndroidScannerFlashManager
) {
    var cameraProvider: ProcessCameraProvider? = null
    var imageAnalysis: ImageAnalysis? = null
    var preview: Preview? = null
    var camera: Camera? = null

    private var isScannerActive = true
    private val _scanDistance = MutableStateFlow(CameraPositionDistance.TOO_FAR)
    private val logger = LoggerFactory.getLogger()
    private var barcodeAnalyzer: MLKitBarcodeAnalyzer? = null

    // ── Analyzer factory — single definition, no duplication ─────────────────

    private fun createAnalyzer(): MLKitBarcodeAnalyzer = MLKitBarcodeAnalyzer(
        config = config,
        eventManager = eventManager,
        stateManager = stateManager,
        isScannerActive = { isScannerActive },
        onPlaySound = { soundManager.playBeepSound() }
    )

    private fun attachAnalyzer(analysis: ImageAnalysis) {
        val analyzer = barcodeAnalyzer ?: createAnalyzer().also { barcodeAnalyzer = it }
        analysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
    }

    private fun releaseAnalyzer() {
        imageAnalysis?.clearAnalyzer()
        barcodeAnalyzer?.close()
        barcodeAnalyzer = null
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    fun startScanning() {
        logger.info("", "Starting scanning")
        isScannerActive = true
        stateManager.updateState(ScannerCameraState.StartingCamera)
        eventManager.emitEvent(ScannerEvent.ScanningStarted)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCamera2Interop::class)
    private fun bindCameraUseCases() {
        if (!isScannerActive) return
        try {
            val provider = cameraProvider ?: return
            releaseAnalyzer()
            provider.unbindAll()

            val previewBuilder = Preview.Builder()
                .setTargetAspectRatio(androidx.camera.core.AspectRatio.RATIO_4_3)
            // Force continuous autofocus at the sensor level so close-up codes on
            // curved/reflective surfaces (e.g. cans) stay sharp enough for MLKit to
            // decode. iOS already does this via AVCaptureFocusModeContinuousAutoFocus;
            // CameraX otherwise leaves focus to unspecified device defaults.
            applyContinuousAutoFocus(Camera2Interop.Extender(previewBuilder))
            preview = previewBuilder.build()
                .also { it.surfaceProvider = previewView.surfaceProvider }

            // Higher analysis resolution than CameraX's low default (~640x480) so dense
            // 1D barcodes on small can labels carry enough detail for MLKit.
            val resolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(
                        Size(1280, 720),
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                    )
                )
                .build()

            val analysisBuilder = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setResolutionSelector(resolutionSelector)
            applyContinuousAutoFocus(Camera2Interop.Extender(analysisBuilder))

            imageAnalysis = analysisBuilder
                .build()
                .also { analysis ->
                    if (isScannerActive) attachAnalyzer(analysis)
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
            flashManager.checkTorchAvailability(camera)

            if (config.advanced.enableMetallicMode) {
                camera?.let { cam ->
                    val exposureState = cam.cameraInfo.exposureState
                    if (exposureState.isExposureCompensationSupported) {
                        val minExposure = exposureState.exposureCompensationRange.lower
                        cam.cameraControl.setExposureCompensationIndex(minExposure)
                        logger.camera("Metallic mode enabled: Exposure set to $minExposure to reduce glare.")
                    }
                }
            }

            camera?.cameraInfo?.zoomState?.observe(lifecycleOwner) { state ->
                eventManager.emitEvent(
                    ScannerEvent.ZoomStateChanged(
                        minZoom = state.minZoomRatio,
                        maxZoom = state.maxZoomRatio,
                        currentZoom = state.zoomRatio
                    )
                )
            }

            stateManager.updateState(ScannerCameraState.CameraReady)
            stateManager.updateState(ScannerCameraState.Scanning)
            logger.camera("Camera bound successfully")

        } catch (exc: Exception) {
            logger.error("CameraX", "Error binding camera use cases", exc)
            val errorMessage = "Could not start camera: ${exc.localizedMessage}"
            eventManager.emitEvent(ScannerEvent.CameraError(errorMessage))
            stateManager.updateState(ScannerCameraState.Error(errorMessage))
        }
    }

    fun pauseScanning() {
        try {
            logger.scanner("Pausing scanning")
            isScannerActive = false
            imageAnalysis?.clearAnalyzer()
            stateManager.updateState(ScannerCameraState.Paused)
            eventManager.emitEvent(ScannerEvent.ScanningPaused)
        } catch (e: Exception) {
            logger.error("CameraX", "Error pausing scanner", e)
        }
    }

    @SuppressLint("MissingPermission")
    fun resumeScanning() {
        try {
            logger.scanner("Resuming scanning")
            if (!isScannerActive && camera != null) {
                isScannerActive = true
                imageAnalysis?.let { attachAnalyzer(it) }
                stateManager.updateState(ScannerCameraState.Scanning)
                eventManager.emitEvent(ScannerEvent.ScanningResumed)
            }
        } catch (e: Exception) {
            logger.error("CameraX", "Error resuming scanner", e)
        }
    }

    fun stopScanning() {
        try {
            logger.scanner("Stopping scanning")
            isScannerActive = false
            camera?.cameraControl?.enableTorch(false)
            releaseAnalyzer()
            camera?.cameraControl?.cancelFocusAndMetering()
            cameraProvider?.unbindAll()
            camera = null
            preview = null
            imageAnalysis = null
            cameraProvider = null
            soundManager.release()
            stateManager.updateState(ScannerCameraState.CameraReady)
            eventManager.emitEvent(ScannerEvent.ScanningStopped)
            logger.scanner("Scanner stopped completely")
        } catch (e: Exception) {
            logger.error("CameraX", "Error stopping scanner", e)
        }
    }

    // ── Camera controls ───────────────────────────────────────────────────────

    fun setZoom(scale: Float) {
        if (!config.behavior.enablePinchToZoom) return
        try {
            val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
            camera?.cameraControl?.setZoomRatio(currentZoomRatio * scale)
        } catch (e: Exception) {
            logger.error("CameraX", "Error setting zoom", e)
        }
    }

    fun setZoomProgress(progress: Float) {
        if (!config.advanced.showZoomControl) return
        try {
            camera?.cameraControl?.setLinearZoom(progress)
            camera?.cameraInfo?.zoomState?.value?.let { state ->
                eventManager.emitEvent(
                    ScannerEvent.ZoomStateChanged(
                        minZoom = state.minZoomRatio,
                        maxZoom = state.maxZoomRatio,
                        currentZoom = state.zoomRatio
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("CameraX", "Error setting linear zoom", e)
        }
    }

    fun setFocus(x: Float, y: Float) {
        if (!config.behavior.enableTapToFocus) return
        try {
            val factory = previewView.meteringPointFactory
            val point = factory.createPoint(x, y)
            // Auto-cancel the tap focus after a short window so the camera returns to
            // continuous autofocus instead of staying locked on the tapped point.
            val action = FocusMeteringAction.Builder(
                point,
                FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
            )
                .setAutoCancelDuration(3, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            camera?.cameraControl?.startFocusAndMetering(action)
        } catch (e: Exception) {
            logger.error("CameraX", "Error setting focus", e)
        }
    }

    /**
     * Applies continuous-picture autofocus (and continuous auto-exposure) to a
     * use-case capture request via Camera2 interop. This mirrors the iOS setup and
     * keeps close-up codes sharp without requiring the user to tap to focus.
     */
    @OptIn(ExperimentalCamera2Interop::class)
    private fun applyContinuousAutoFocus(extender: Camera2Interop.Extender<*>) {
        extender.setCaptureRequestOption(
            CaptureRequest.CONTROL_AF_MODE,
            CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        extender.setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_MODE,
            CameraMetadata.CONTROL_AE_MODE_ON
        )
    }
}
