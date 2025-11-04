package io.github.ismoy.imagepickerkmp.presentation.ui.utils

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.presentation.viewModel.ImagePickerViewModel


@Composable
fun rememberImagePickerViewModel(): ImagePickerViewModel {
    return remember { 
        ImagePickerViewModel(
            logger = DefaultLogger
        )
    }
}


@Composable
fun rememberCameraManager(
    context: Context,
    activity: ComponentActivity
): CameraXManager? {
    return remember(context, activity) {
        try {
            val fileManager = FileManager(context)
            val orientationCorrector = ImageOrientationCorrector()
            val imageProcessor = ImageProcessor(context, fileManager, orientationCorrector)
            val cameraController = CameraController(context, activity, fileManager)
            
            CameraXManager(cameraController, imageProcessor)
        } catch (e: Exception) {
            Log.e("ImagePickerKMP", "Failed to create camera manager", e)
            null
        }
    }
}
