package io.github.ismoy.imagepickerkmp.features.ocr.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.features.ocr.model.ImagePickerOCRConfig
import io.github.ismoy.imagepickerkmp.features.ocr.annotations.ExperimentalOCRApi

@Suppress(names = ["FunctionNaming"])
@Composable
@ExperimentalOCRApi
actual fun ImagePickerLauncherOCR(config: ImagePickerOCRConfig) {
    LaunchedEffect(Unit) {
        config.onError(
            UnsupportedOperationException(
                "OCR functionality is not yet implemented for JVM/Desktop platform. " +
                "This feature is currently available only on Android and iOS."
            )
        )
    }
    Text("OCR not available in this platform")
}