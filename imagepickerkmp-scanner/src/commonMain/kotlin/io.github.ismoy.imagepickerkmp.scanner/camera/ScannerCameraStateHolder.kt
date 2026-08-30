package io.github.ismoy.imagepickerkmp.scanner.camera

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.scanner.PlatformScannerDependencies
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEventManager
import io.github.ismoy.imagepickerkmp.scanner.createScannerCaptureManager
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode
import io.github.ismoy.imagepickerkmp.scanner.ui.ScannerCameraUIState

@Stable
class ScannerCameraStateHolder(
    val config: ScannerCameraConfig,
    private val onCodeScanned: (String, String?) -> Unit,
    private val onCameraError: (String) -> Unit,
    private val onPermissionPermanentlyDenied: () -> Unit,
    val eventManager: ScannerEventManager = ScannerEventManager(),
    val stateManager: DefaultScannerCameraStateManager = DefaultScannerCameraStateManager()
) : ScannerCameraUIState {
    var hasPermission by mutableStateOf(false)
        internal set

    var permissionDeniedPermanently by mutableStateOf(false)
        internal set

    var scanner by mutableStateOf<ScannerCaptureManager?>(null)
        internal set

    var isCameraInactive by mutableStateOf(false)
        internal set

    override var flashMode by mutableStateOf(FlashMode.OFF)
        internal set

    var distance by mutableStateOf(CameraPositionDistance.TOO_FAR)
        internal set

    var minZoom by mutableStateOf(1f)
        internal set

    var maxZoom by mutableStateOf(1f)
        internal set

    var currentZoom by mutableStateOf(1f)
        internal set

    var detectedBarcodes by mutableStateOf<List<BarcodeData>>(emptyList())
        internal set

    private var sessionIsScanning by mutableStateOf(false)
    private var latestScannedCode by mutableStateOf<String?>(null)

    override val isScanning: Boolean
        get() = sessionIsScanning

    override val lastScannedCode: String?
        get() = latestScannedCode

    private val inactivityManager = ScannerInactivityManager(
        config = config,
        onInactivity = {
            isCameraInactive = true
            stopScanning()
        }
    )

    private val autoZoomController = ScannerAutoZoomController(config = config)
    internal val eventListener = ScannerEventProcessor(
        onCodeScanned = { code, format ->
            latestScannedCode = code
            onCodeScanned(code, format)
        },
        onCameraError = { message ->
            sessionIsScanning = false
            onCameraError(message)
        },
        onPermissionResult = { granted -> hasPermission = granted },
        onPermissionPermanentlyDenied = {
            permissionDeniedPermanently = true
            sessionIsScanning = false
            onPermissionPermanentlyDenied()
        },
        onDistanceChanged = { newDistance -> distance = newDistance },
        onFlashStateChanged = { mode -> flashMode = mode },
        onCameraStateChanged = { state ->
            sessionIsScanning = state is ScannerCameraState.Scanning
            stateManager.updateState(state)
        },
        onZoomStateChanged = { min, max, current ->
            minZoom = min
            maxZoom = max
            currentZoom = current
        },
        onBarcodesDetected = { barcodes ->
            detectedBarcodes = barcodes
            autoZoomController.handleAutoZoom(
                barcodes = barcodes,
                currentZoom = currentZoom,
                maxZoom = maxZoom,
                scanner = scanner
            )
            if (barcodes.isNotEmpty()) updateActivity()
        }
    )

    fun updateActivity() {
        inactivityManager.updateActivity(
            isCameraInactive = isCameraInactive,
            onReactivate = {
                isCameraInactive = false
                startScanning()
            }
        )
    }

    fun initializeScanner(dependencies: PlatformScannerDependencies) {
        latestScannedCode = null
        sessionIsScanning = false
        scanner = createScannerCaptureManager(
            dependencies = dependencies,
            config = config,
            eventManager = eventManager,
            stateManager = stateManager
        )
    }

    fun startScanning() {
        scanner?.startScanning()
        inactivityManager.resetInactivityTimer()
    }

    override fun toggleFlash() {
        flashMode = if (flashMode == FlashMode.ON) FlashMode.OFF else FlashMode.ON
        scanner?.toggleFlash()
    }

    override fun resumeScanning() {
        scanner?.resumeScanning()
        inactivityManager.resetInactivityTimer()
    }

    override fun pauseScanning() {
        sessionIsScanning = false
        scanner?.pauseScanning()
        inactivityManager.cancelTimer()
    }

    override fun stopScanning() {
        sessionIsScanning = false
        scanner?.stopScanning()
        inactivityManager.cancelTimer()
    }

    fun dispose() {
        eventManager.removeListener(eventListener)
        stopScanning()
        inactivityManager.dispose()
    }
}
