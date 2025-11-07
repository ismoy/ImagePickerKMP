package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery.GalleryFileProcessor.processSelectedFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Launcher for single file selection from gallery.
 */
@Composable
internal fun rememberSinglePickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    if (uri != null) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val result = processSelectedFile(context, uri, compressionLevel, includeExif)
                if (result != null) {
                    onPhotoSelected(result)
                } else {
                    onError(Exception(getStringResource(StringResource.GALLERY_SELECTION_ERROR)))
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    } else {
        onDismiss()
    }
}

/**
 * Launcher for multiple file selection from gallery.
 * Optimized with parallel processing (max 3 concurrent) for better performance.
 */
@Composable
internal fun rememberMultiplePickerLauncher(
    context: Context,
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
) { uris: List<Uri> ->
    if (uris.isNotEmpty()) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val semaphore = Semaphore(3)
                val results = mutableListOf<GalleryPhotoResult>()
                val errors = mutableListOf<Exception>()
                
                val deferredResults = uris.map { uri ->
                    async(Dispatchers.IO) {
                        semaphore.withPermit {
                            try {
                                processSelectedFile(context, uri, compressionLevel, includeExif)
                            } catch (e: Exception) {
                                errors.add(e)
                                null
                            }
                        }
                    }
                }
                
                results.addAll(deferredResults.awaitAll().filterNotNull())
                
                if (errors.isNotEmpty()) {
                    errors.forEach { onError(it) }
                }
                
                if (results.isNotEmpty()) {
                    onPhotosSelected(results)
                } else {
                    onError(Exception(getStringResource(StringResource.GALLERY_SELECTION_ERROR)))
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    } else {
        onDismiss()
    }
}
