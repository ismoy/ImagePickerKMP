package io.github.ismoy.imagepickerkmp

import android.content.Context
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

class ImageProcessor(context: Context) {
    private val fileManager = FileManager(context)

    fun processImage(
        imageFile: File,
        onPhotoCaptured: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                if (bitmap != null) {
                    val result = PhotoResult(
                        uri = fileManager.fileToUriString(imageFile),
                        width = bitmap.width,
                        height = bitmap.height
                    )
                    withContext(Dispatchers.Main) {
                        onPhotoCaptured(result)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError(PhotoCaptureException("Failed to decode captured image."))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(PhotoCaptureException("Failed to process image: ${e.message}"))
                }
            }
        }
    }
}