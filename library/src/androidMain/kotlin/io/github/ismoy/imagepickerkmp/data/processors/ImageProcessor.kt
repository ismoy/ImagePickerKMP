package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.BitmapFactory
import io.github.ismoy.imagepickerkmp.data.camera.CameraController.CameraType
import io.github.ismoy.imagepickerkmp.domain.exceptions.ImageProcessingException
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Handles image processing operations such as orientation correction and photo result creation.
 * 
 * SOLID: Single Responsibility - Only handles image processing operations
 * SOLID: Dependency Inversion - Dependencies injected via constructor
 */
class ImageProcessor(
    private val fileManager: io.github.ismoy.imagepickerkmp.data.managers.FileManager,
    private val orientationCorrector: ImageOrientationCorrector
) {

    fun processImage(
        imageFile: File,
        cameraType: CameraType,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val correctedImageFile = orientationCorrector.correctImageOrientation(imageFile, cameraType)
                val bitmap = BitmapFactory.decodeFile(correctedImageFile.absolutePath)
                if (bitmap != null) {
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(correctedImageFile),
                        width = bitmap.width,
                        height = bitmap.height
                    )
                    withContext(Dispatchers.Main) {
                        onPhotoCaptured(result)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        ImageProcessingException("Failed to decode captured image.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(ImageProcessingException("Failed to process image: ${e.message}", e))
                }
            }
        }
    }
}
