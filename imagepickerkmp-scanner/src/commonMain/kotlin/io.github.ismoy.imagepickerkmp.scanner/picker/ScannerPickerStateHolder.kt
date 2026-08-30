package io.github.ismoy.imagepickerkmp.scanner.picker

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.core.language.getLanguageDevice
import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerPickerError
import io.github.ismoy.imagepickerkmp.scanner.capture.ScannerPickerState
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult

@Stable
class ScannerPickerStateHolder internal constructor(
    internal val config: ScannerPickerConfig
) {
    init {
        I18nKonfig.setLocale(getLanguageDevice())
    }

    var result: ScannerPickerState by mutableStateOf(ScannerPickerState.Idle)
        internal set

    internal var activeMode by mutableStateOf<ScannerPickerMode>(ScannerPickerMode.None)

    private val _scannedCodes = mutableStateListOf<ScannerResult>()
    val scannedCodes: List<ScannerResult> get() = _scannedCodes

    fun launchScanner(
        onDismiss: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        if (activeMode != ScannerPickerMode.None && result is ScannerPickerState.Loading) return
        _scannedCodes.clear()
        result = ScannerPickerState.Loading
        activeMode = ScannerPickerMode.Camera(onDismiss = onDismiss, onError = onError)
    }

    fun reset() {
        result = ScannerPickerState.Idle
        activeMode = ScannerPickerMode.None
        _scannedCodes.clear()
    }

    internal fun notifySuccess(scannerResult: ScannerResult) {
        result = ScannerPickerState.Success(scannerResult)
        activeMode = ScannerPickerMode.None
    }

    internal fun notifyBatchSuccess(scannerResults: List<ScannerResult>) {
        result = ScannerPickerState.BatchSuccess(scannerResults.toList())
        activeMode = ScannerPickerMode.None
    }

    internal fun addScannedCode(scannerResult: ScannerResult) {
        if (!config.camera.behavior.allowDuplicates) {
            if (_scannedCodes.any { it.code == scannerResult.code }) return
        }
        _scannedCodes.add(scannerResult)
    }

    internal fun notifyDismiss() {
        result = ScannerPickerState.Cancelled
        activeMode = ScannerPickerMode.None
        _scannedCodes.clear()
    }

    internal fun notifyError(error: ScannerPickerError) {
        result = ScannerPickerState.Error(error)
        activeMode = ScannerPickerMode.None
    }
}
