package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser

/**
 * Common file picker utility for JVM platform that handles both single and multiple file selection.
 * This composable function provides a unified interface for file selection operations.
 *
 * @param onPhotosSelected Callback when photos are successfully selected
 * @param onError Callback when an error occurs during file selection
 * @param onDismiss Callback when the file picker is dismissed without selection
 * @param allowMultiple Whether to allow multiple file selection
 * @param mimeTypes List of allowed MIME types for file filtering
 * @param selectionLimit Maximum number of files that can be selected (only applies when allowMultiple is true)
 * @param fileFilterDescription Description for the file filter in the dialog
 */
@Composable
fun JvmFilePicker(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    selectionLimit: Long = 10,
    fileFilterDescription: String = "Images"
) {
    val scope = rememberCoroutineScope()
    val launchKey = remember {
        "${System.nanoTime()}-${onPhotosSelected.hashCode()}-${onError.hashCode()}"
    }
    
    LaunchedEffect(launchKey) {
        withContext(Dispatchers.IO) {
            try {
                val completableResult = kotlinx.coroutines.CompletableDeferred<List<GalleryPhotoResult>>()
                javax.swing.SwingUtilities.invokeLater {
                    try {
                        val results = if (allowMultiple) {
                            selectMultipleImages(mimeTypes, selectionLimit, fileFilterDescription)
                        } else {
                            val result = selectSingleImage(mimeTypes, fileFilterDescription)
                            if (result != null) listOf(result) else emptyList()
                        }
                        completableResult.complete(results)
                        
                    } catch (e: Exception) {
                        completableResult.completeExceptionally(e)
                    }
                }
                
                val results = completableResult.await()
                scope.launch(Dispatchers.Main) {
                    if (results.isNotEmpty()) {
                        onPhotosSelected(results)
                    } else {
                        onDismiss()
                    }
                }
                
            } catch (e: Exception) {
                scope.launch(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}


private fun selectSingleImage(mimeTypes: List<MimeType>, filterDescription: String): GalleryPhotoResult? {
    return try {
        val fileChooser = createFileChooser(mimeTypes, "Select Image", false, filterDescription)
        val result = fileChooser.showOpenDialog(null)
        
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            createGalleryPhotoResult(selectedFile)
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}


private fun selectMultipleImages(mimeTypes: List<MimeType>, selectionLimit: Long, filterDescription: String): List<GalleryPhotoResult> {
    return try {
        val fileChooser = createFileChooser(mimeTypes, "Select Images", true, filterDescription)
        val result = fileChooser.showOpenDialog(null)
        
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFiles = fileChooser.selectedFiles.take(selectionLimit.toInt())
            selectedFiles.mapNotNull { file ->
                createGalleryPhotoResult(file)
            }
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}
