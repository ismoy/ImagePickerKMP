package io.github.ismoy.imagepickerkmp.features.ocr.presentation

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.features.ocr.annotations.ExperimentalOCRApi
import io.github.ismoy.imagepickerkmp.features.ocr.model.ImagePickerOCRConfig

@Suppress("FunctionNaming")
@Composable
@ExperimentalOCRApi
actual fun ImagePickerLauncherOCR(config: ImagePickerOCRConfig) {
    // OCR is not supported on iOS — no-op.
}
