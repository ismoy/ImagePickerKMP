package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraState
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraStateHolder
import io.github.ismoy.imagepickerkmp.scanner.camera.config.InactiveOverlayStyle
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.utils.ActiveScanningOverlay
import io.github.ismoy.imagepickerkmp.scanner.utils.EnterpriseInactiveOverlay
import io.github.ismoy.imagepickerkmp.scanner.utils.InactiveScanningOverlay

@Composable
internal fun ScannerCamera(
    modifier: Modifier = Modifier.fillMaxSize(),
    config: ScannerCameraConfig = ScannerCameraConfig.default(),
    uiExtensions: ScannerUIExtensions = ScannerUIExtensions(),
    onCodeScanned: (String, String?) -> Unit = { _, _ -> },
    onBatchDone: () -> Unit = {},
    onCameraError: (String) -> Unit = {},
    onPermissionResult: (Boolean) -> Unit = {},
    onPermissionPermanentlyDenied: () -> Unit = {},
    onStateChanged: (ScannerCameraState) -> Unit = {},
    onClose: (() -> Unit)? = null,
    scannedCount: Int = 0
) {
    val stateHolder = remember(config) {
        ScannerCameraStateHolder(
            config = config,
            onCodeScanned = onCodeScanned,
            onCameraError = onCameraError,
            onPermissionPermanentlyDenied = onPermissionPermanentlyDenied
        )
    }

    LaunchedEffect(stateHolder.stateManager) {
        stateHolder.stateManager.currentState.collect { state ->
            onStateChanged(state)
        }
    }

    DisposableEffect(stateHolder) {
        stateHolder.eventManager.addListener(stateHolder.eventListener)

        onDispose {
            stateHolder.dispose()
        }
    }
    LaunchedEffect(stateHolder.scanner) {
        stateHolder.startScanning()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    if (zoom != 1f) {
                        stateHolder.scanner?.setZoom(zoom)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    stateHolder.scanner?.setFocus(offset.x, offset.y)
                }
            }
    ) {
        PlatformScannerCameraRenderer(
            onPreviewViewReady = { dependencies ->
                stateHolder.initializeScanner(
                    dependencies = dependencies
                )
            },
            scanner = stateHolder.scanner,
            modifier = Modifier.fillMaxSize(),
            onUserInteraction = { stateHolder.updateActivity() }
        )

        if (stateHolder.isCameraInactive) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        }

        if (config.advanced.batchMode && (config.advanced.enableAROverlays || config.advanced.enablePickAndPack)) {
            ScannerAROverlay(
                barcodes = stateHolder.detectedBarcodes,
                modifier = Modifier.fillMaxSize(),
                customBarcodeContent = config.advanced.customBarcodeContent,
                onBarcodeTap = if (config.advanced.enablePickAndPack) {
                    { barcode ->
                        if (config.behavior.playSound) {
                            io.github.ismoy.imagepickerkmp.scanner.utils.playScannerSystemBeep()
                        }
                        stateHolder.eventManager.emitEvent(
                            io.github.ismoy.imagepickerkmp.scanner.capture.ScannerEvent.
                            CodeScanned(barcode.rawValue, barcode.format.name)
                        )
                    }
                } else null
            )
        }

        if (uiExtensions.customLayout != null) {
            uiExtensions.customLayout.invoke(stateHolder)
        } else {
            if (!stateHolder.isCameraInactive) {
                if (uiExtensions.customOverlay != null) {
                    uiExtensions.customOverlay.invoke(stateHolder)
                } else {
                    val scanDistance = stateHolder.distance
                    ActiveScanningOverlay(
                        watermark = config.ui.watermark,
                        scanDistance = scanDistance,
                        tooFarColor = config.ui.tooFarColor,
                        tooCloseColor = config.ui.tooCloseColor,
                        tooOptimalColor = config.ui.tooOptimalColor,
                        tooFarText = config.ui.tooFarText,
                        tooCloseText = config.ui.tooCloseText,
                        tooOptimalText = config.ui.tooOptimalText,
                        showScanLine = config.ui.showScanLine,
                        scanLineColor = config.ui.scanLineColor,
                        overlayCornerRadius = config.ui.overlayCornerRadius,
                        overlayStyle = config.ui.overlayStyle
                    )
                }

                ScannerTopBar(
                    modifier = Modifier.align(Alignment.TopCenter),
                    onClose = onClose,
                    config = config,
                    stateHolder = stateHolder
                )

                ScannerBottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    config = config,
                    stateHolder = stateHolder,
                    scannedCount = scannedCount,
                    onBatchDone = onBatchDone
                )

                if (config.advanced.showZoomControl && stateHolder.maxZoom > stateHolder.minZoom) {
                    val zoomProgress = (stateHolder.currentZoom - stateHolder.minZoom) / (stateHolder.maxZoom - stateHolder.minZoom)
                    ScannerZoomControl(
                        zoomProgress = zoomProgress,
                        currentZoom = stateHolder.currentZoom,
                        onZoomChange = {
                            stateHolder.scanner?.setZoomProgress(it)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 240.dp)
                    )
                }
            }

            if (config.behavior.enableInactivity && stateHolder.isCameraInactive) {
                if (uiExtensions.customInactiveOverlay != null) {
                    uiExtensions.customInactiveOverlay.invoke(stateHolder)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (config.ui.inactiveOverlayStyle == InactiveOverlayStyle.ENTERPRISE) {
                            EnterpriseInactiveOverlay(
                                onTap = { stateHolder.updateActivity() },
                                tapText = config.ui.inactiveModeText,
                                config = config.ui.enterpriseOverlayConfig
                            )
                        } else {
                            InactiveScanningOverlay(
                                onTap = { stateHolder.updateActivity() },
                                tapText = config.ui.inactiveModeText
                            )
                        }
                    }
                }
            }
        }
    }
}
