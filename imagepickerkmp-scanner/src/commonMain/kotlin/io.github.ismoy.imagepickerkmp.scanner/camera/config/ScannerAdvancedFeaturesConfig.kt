package io.github.ismoy.imagepickerkmp.scanner.camera.config

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData

data class ScannerAdvancedFeaturesConfig(
    val batchMode: Boolean = false,
    val enableMetallicMode: Boolean = false,
    val showZoomControl: Boolean = true,
    val enableAROverlays: Boolean = false,
    val enablePickAndPack: Boolean = false,
    val enableAutoZoom: Boolean = false,
    val customBarcodeContent: (@Composable (BarcodeData) -> Unit)? = null
)
