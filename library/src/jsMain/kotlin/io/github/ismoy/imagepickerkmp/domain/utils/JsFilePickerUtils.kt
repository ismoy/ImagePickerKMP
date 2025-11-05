package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader

/**
 * Common file picker utility for JS platform that handles both single and multiple file selection.
 * This composable function provides a unified interface for file selection operations.
 *
 * @param onPhotosSelected Callback when photos are successfully selected
 * @param onError Callback when an error occurs during file selection
 * @param onDismiss Callback when the file picker is dismissed without selection
 * @param allowMultiple Whether to allow multiple file selection
 * @param mimeTypes List of allowed MIME types for file filtering
 * @param selectionLimit Maximum number of files that can be selected (only applies when allowMultiple is true)
 * @param fileFilterDescription Description for the file filter in the dialog (not used in JS but kept for consistency)
 */
@Composable
fun JsFilePicker(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
    selectionLimit: Long = 10,
    fileFilterDescription: String = "Images"
) {
    val launchKey = remember {
        "${js("Date.now()")}-${onPhotosSelected.hashCode()}-${onError.hashCode()}"
    }
    
    LaunchedEffect(launchKey) {
        try {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = mimeTypes.joinToString(",") { it.value }
            input.multiple = allowMultiple
            
            input.onchange = { event ->
                try {
                    val files = input.files
                    if (files != null && files.length > 0) {
                        val results = mutableListOf<GalleryPhotoResult>()
                        var processedCount = 0
                        val totalFiles = minOf(files.length, selectionLimit.toInt())
                        
                        for (i in 0 until totalFiles) {
                            val file = files.item(i)
                            if (file != null) {
                                val reader = FileReader()
                                reader.onload = { loadEvent ->
                                    try {
                                        val result = loadEvent.target?.asDynamic()?.result as? String
                                        if (result != null) {
                                            val galleryResult = GalleryPhotoResult(
                                                uri = result,
                                                width = 0,
                                                height = 0,
                                                fileName = file.name,
                                                fileSize = file.size.toLong()
                                            )
                                            results.add(galleryResult)
                                        }
                                        
                                        processedCount++
                                        if (processedCount == totalFiles) {
                                            onPhotosSelected(results)
                                        }
                                    } catch (e: Exception) {
                                        onError(e)
                                    }
                                }
                                reader.readAsDataURL(file)
                            }
                        }
                    } else {
                        onDismiss()
                    }
                } catch (e: Exception) {
                    onError(e)
                }
            }
            
            input.click()
            
        } catch (e: Exception) {
            onError(e)
        }
    }
}
