package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerResult

sealed interface ScannerPickerState {
    data object Idle : ScannerPickerState
    data object Loading : ScannerPickerState
    data class Success(val result: ScannerResult) : ScannerPickerState
    data class BatchSuccess(val results: List<ScannerResult>) : ScannerPickerState
    data class Error(val error: ScannerPickerError) : ScannerPickerState
    data object Cancelled : ScannerPickerState
}
