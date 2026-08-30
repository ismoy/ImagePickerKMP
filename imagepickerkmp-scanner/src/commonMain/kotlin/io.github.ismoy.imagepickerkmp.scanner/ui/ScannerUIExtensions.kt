package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData

data class ScannerUIExtensions(
    val customOverlay: (@Composable (ScannerCameraUIState) -> Unit)? = null,
    val customFlashButton: (@Composable (ScannerCameraUIState) -> Unit)? = null,
    val customBatchDoneButton: (@Composable (ScannerCameraUIState) -> Unit)? = null,
    val customInactiveOverlay: (@Composable (ScannerCameraUIState) -> Unit)? = null,
    val customLayout: (@Composable (ScannerCameraUIState) -> Unit)? = null
)
