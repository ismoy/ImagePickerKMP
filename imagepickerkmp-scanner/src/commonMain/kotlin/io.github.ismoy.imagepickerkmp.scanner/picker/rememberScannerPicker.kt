package io.github.ismoy.imagepickerkmp.scanner.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.core.language.getLanguageDevice
import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerPickerError
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult
import io.github.ismoy.imagepickerkmp.scanner.domain.parseBarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.ui.RequestScannerCameraPermission
import io.github.ismoy.imagepickerkmp.scanner.ui.ScannerCamera
import io.github.ismoy.imagepickerkmp.scanner.utils.getCurrentTimeMillis

@Composable
fun rememberScannerPicker(
    config: ScannerPickerConfig = ScannerPickerConfig.default()
): ScannerPickerStateHolder {
    I18nKonfig.setLocale(getLanguageDevice())
    val holder = remember { ScannerPickerStateHolder(config) }
    val currentHolder = rememberUpdatedState(holder)
    val currentConfig = rememberUpdatedState(config)

    val onScannerResult: (String, String?) -> Unit = remember(holder) {
        { code, format ->
            val result = ScannerResult(
                code = code,
                format = parseBarcodeFormat(format),
                timestamp = getCurrentTimeMillis()
            )
            if (currentConfig.value.camera.advanced.batchMode) {
                currentHolder.value.addScannedCode(result)
            } else {
                currentHolder.value.notifySuccess(result)
            }
        }
    }

    when (val mode = holder.activeMode) {
        is ScannerPickerMode.Camera -> {
            val finishDismiss: () -> Unit = remember(mode, holder) {
                {
                    if (holder.activeMode === mode) {
                        holder.notifyDismiss()
                        mode.onDismiss?.invoke()
                    }
                }
            }
            val finishError: (Exception) -> Unit = remember(mode, holder) {
                { exception ->
                    if (holder.activeMode === mode) {
                        val error = exception as? ScannerPickerError
                            ?: ScannerPickerError.Unknown(exception)
                        holder.notifyError(error)
                        mode.onError?.invoke(exception)
                    }
                }
            }

            Dialog(
                onDismissRequest = finishDismiss,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                var permissionGranted by remember { mutableStateOf(false) }

                RequestScannerCameraPermission(
                    dialogConfig = config.permissions,
                    onPermissionPermanentlyDenied = {
                        finishError(ScannerPickerError.PermissionDenied())
                    },
                    onResult = { granted -> permissionGranted = granted },
                    customPermissionHandler = null
                )

                if (permissionGranted) {
                    ScannerCamera(
                        config = currentConfig.value.camera,
                        uiExtensions = currentConfig.value.uiExtensions,
                        onCodeScanned = onScannerResult,
                        onBatchDone = {
                            val results = currentHolder.value.scannedCodes
                            currentHolder.value.notifyBatchSuccess(results)
                        },
                        onCameraError = { errorMessage ->
                            finishError(ScannerPickerError.CameraError(errorMessage))
                        },
                        onPermissionPermanentlyDenied = {
                            finishError(ScannerPickerError.PermissionDenied())
                        },
                        onClose = finishDismiss,
                        scannedCount = currentHolder.value.scannedCodes.size
                    )
                }
            }
        }
        is ScannerPickerMode.None -> Unit
    }

    return holder
}
