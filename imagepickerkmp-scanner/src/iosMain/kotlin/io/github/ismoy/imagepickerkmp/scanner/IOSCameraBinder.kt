package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.camera.DefaultScannerCameraStateManager
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.camera
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.configureInitialState
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.getBackCameraDevice
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.updateFocusPoint
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.updateZoomByProgress
import io.github.ismoy.imagepickerkmp.scanner.utils.extensions.updateZoomByScale
import io.github.ismoy.imagepickerkmp.scanner.utils.scanner
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create
import platform.darwin.dispatch_queue_t

@OptIn(ExperimentalForeignApi::class)
internal class IOSCameraBinder(
    private val previewView: UIView,
    private val config: ScannerCameraConfig,
    private val eventManager: ScannerEventManager,
    private val stateManager: DefaultScannerCameraStateManager,
    private val soundManager: IOSScannerSoundManager
) {
    var captureSession: AVCaptureSession? = null
        private set
    var previewLayer: AVCaptureVideoPreviewLayer? = null
        private set

    private val logger = LoggerFactory.getLogger()
    private val sessionQueue: dispatch_queue_t =
        dispatch_queue_create("io.github.ismoy.imagepickerkmp.scanner.session", null)

    private var isScanning = false
    private var sessionGeneration = 0L

    private val analyzer = IOSBarcodeAnalyzer(
        config = config,
        eventManager = eventManager,
        stateManager = stateManager,
        soundManager = soundManager,
        getPreviewLayer = { previewLayer },
        getIsScanning = { isScanning }
    )

    /** Creates a fully configured session on the UI thread, without publishing partial state. */
    private fun setupCamera(): AVCaptureSession? {
        try {
            logger.camera("Setting up camera")
            val device = getBackCameraDevice()
            if (device == null) {
                reportCameraError("Cannot find a back camera device")
                return null
            }

            val session = AVCaptureSession().apply {
                sessionPreset = AVCaptureSessionPresetHigh
            }
            device.configureInitialState(eventManager)
            val input = AVCaptureDeviceInput(device = device, error = null)
            if (!session.canAddInput(input)) {
                reportCameraError("Cannot add camera input to the capture session")
                return null
            }
            session.addInput(input)

            val layer = AVCaptureVideoPreviewLayer(session = session).apply {
                videoGravity = AVLayerVideoGravityResizeAspectFill
                frame = previewView.bounds
            }
            previewView.layer.sublayers?.forEach { existing ->
                if (existing is AVCaptureVideoPreviewLayer) existing.removeFromSuperlayer()
            }
            previewView.layer.addSublayer(layer)

            captureSession = session
            previewLayer = layer
            logger.camera("Camera setup completed")
            return session
        } catch (exception: Exception) {
            reportCameraError("Could not set up camera: ${exception.message ?: "Unknown error"}", exception)
            return null
        }
    }

    private fun reportCameraError(message: String, exception: Exception? = null) {
        if (exception == null) {
            logger.error("Camera", message)
        } else {
            logger.error("Camera", message, exception)
        }
        isScanning = false
        eventManager.emitEvent(ScannerEvent.CameraError(message))
        stateManager.updateState(ScannerCameraState.Error(message))
    }

    fun startScanning() {
        logger.scanner("Starting scanning")
        val generation = ++sessionGeneration
        stateManager.updateState(ScannerCameraState.StartingCamera)

        val existingSession = captureSession
        val session = existingSession ?: setupCamera() ?: return
        if (existingSession == null) analyzer.attachToSession(session)

        dispatch_async(sessionQueue) {
            session.startRunning()
            dispatch_async(dispatch_get_main_queue()) {
                if (generation == sessionGeneration && captureSession === session) {
                    isScanning = true
                    stateManager.updateState(ScannerCameraState.CameraReady)
                    stateManager.updateState(ScannerCameraState.Scanning)
                    eventManager.emitEvent(ScannerEvent.ScanningStarted)
                }
            }
        }
    }

    fun stopScanning() {
        logger.scanner("Stopping scanning")
        sessionGeneration++
        isScanning = false
        soundManager.release()

        val sessionToStop = captureSession
        captureSession = null
        previewLayer?.removeFromSuperlayer()
        previewLayer = null
        analyzer.clearState()

        dispatch_async(sessionQueue) {
            sessionToStop?.stopRunning()
        }
        stateManager.updateState(ScannerCameraState.CameraReady)
        eventManager.emitEvent(ScannerEvent.ScanningStopped)
    }

    fun pauseScanning() {
        logger.scanner("Pausing scanning")
        val session = captureSession ?: return
        sessionGeneration++
        isScanning = false
        previewLayer?.setHidden(true)
        dispatch_async(sessionQueue) {
            session.stopRunning()
        }
        stateManager.updateState(ScannerCameraState.Paused)
        eventManager.emitEvent(ScannerEvent.ScanningPaused)
    }

    fun resumeScanning() {
        logger.scanner("Resuming scanning")
        val session = captureSession ?: run {
            startScanning()
            return
        }
        val generation = ++sessionGeneration
        previewLayer?.setHidden(false)
        dispatch_async(sessionQueue) {
            session.startRunning()
            dispatch_async(dispatch_get_main_queue()) {
                if (generation == sessionGeneration && captureSession === session) {
                    isScanning = true
                    stateManager.updateState(ScannerCameraState.Scanning)
                    eventManager.emitEvent(ScannerEvent.ScanningResumed)
                }
            }
        }
    }

    fun setZoom(scale: Float) {
        if (!config.behavior.enablePinchToZoom) return
        getBackCameraDevice()?.updateZoomByScale(scale, eventManager)
    }

    fun setZoomProgress(progress: Float) {
        if (!config.advanced.showZoomControl) return
        getBackCameraDevice()?.updateZoomByProgress(progress, eventManager)
    }

    fun setFocus(x: Float, y: Float) {
        if (!config.behavior.enableTapToFocus) return
        getBackCameraDevice()?.updateFocusPoint(x, y)
    }
}
