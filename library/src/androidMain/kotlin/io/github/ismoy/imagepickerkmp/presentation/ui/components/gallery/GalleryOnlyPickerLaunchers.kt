package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
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
 * Custom contract for picking images specifically from gallery (not file manager).
 * This contract uses ACTION_PICK with MediaStore to ensure gallery is opened.
 */
class PickImageFromGallery : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, input)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}

/**
 * Custom contract for picking multiple images specifically from gallery.
 */
class PickMultipleImagesFromGallery : ActivityResultContract<String, List<Uri>>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, input)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()
        
        intent?.let {
            // Handle multiple selection
            it.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i).uri?.let { uri ->
                        uris.add(uri)
                    }
                }
            }
            // Handle single selection
            it.data?.let { uri ->
                if (uris.isEmpty()) {
                    uris.add(uri)
                }
            }
        }
        
        return uris
    }
}

/**
 * Launcher for single image selection specifically from gallery.
 */
@Composable
internal fun rememberGalleryOnlyPickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false
) = rememberLauncherForActivityResult(
    contract = PickImageFromGallery()
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
 * Launcher for multiple image selection specifically from gallery.
 */
@Composable
internal fun rememberGalleryOnlyMultiplePickerLauncher(
    context: Context,
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false
) = rememberLauncherForActivityResult(
    contract = PickMultipleImagesFromGallery()
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
                
                val finalResults = deferredResults.awaitAll().filterNotNull()
                results.addAll(finalResults)
                
                if (results.isNotEmpty()) {
                    onPhotosSelected(results)
                } else if (errors.isNotEmpty()) {
                    onError(errors.first())
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
