package io.github.ismoy.imagepickerkmp.features.ocr.presentation

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.features.ocr.model.ImagePickerOCRConfig
import io.github.ismoy.imagepickerkmp.features.ocr.annotations.ExperimentalOCRApi


@Suppress("FunctionNaming")
@Composable
@ExperimentalOCRApi
expect fun ImagePickerLauncherOCR(
    config: ImagePickerOCRConfig
)
