package io.github.ismoy.imagepickerkmp.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.camera.AndroidPhotoCaptureManager
import io.github.ismoy.imagepickerkmp.camera.FileManager
import io.github.ismoy.imagepickerkmp.camera.ImageProcessor


@Composable
internal fun rememberPhotoCaptureManager(context: Context): AndroidPhotoCaptureManager {
    return remember {
        val fileManager = FileManager()
        val imageProcessor = ImageProcessor(context, fileManager)
        AndroidPhotoCaptureManager(context, fileManager, imageProcessor)
    }
}
