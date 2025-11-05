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
import kotlinx.coroutines.launch

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
                val results = mutableListOf<GalleryPhotoResult>()
                for (uri in uris) {
                    try {
                        val result = processSelectedFile(context, uri, compressionLevel, includeExif)
                        if (result != null) results.add(result)
                    } catch (e: Exception) {
                        onError(e)
                    }
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
