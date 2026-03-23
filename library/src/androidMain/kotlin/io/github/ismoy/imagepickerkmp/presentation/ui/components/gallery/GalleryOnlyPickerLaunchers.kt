package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import imagepickerkmp.library.generated.resources.Res
import imagepickerkmp.library.generated.resources.gallery_selection_error
import imagepickerkmp.library.generated.resources.mime_type_mismatch_error
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.data.gallery.GalleryFileProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jetbrains.compose.resources.stringResource

internal fun uriMatchesMimeTypes(context: Context, uri: Uri, allowedMimeTypes: Array<String>): Boolean {
    if (allowedMimeTypes.any { it == "*/*" || it == "image/*" }) return true

    val actualMimeType = context.contentResolver.getType(uri)?.lowercase() ?: return false

    return allowedMimeTypes.any { allowed ->
        when {
            allowed.endsWith("/*") -> actualMimeType.startsWith(allowed.removeSuffix("*"))
            else -> actualMimeType == allowed.lowercase()
        }
    }
}

internal class PickImageFromGallery : ActivityResultContract<Array<String>, Uri?>() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            if (input.size == 1) {
                type = input[0]
            } else {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, input)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != android.app.Activity.RESULT_OK) {
            return null
        }
        return intent?.data
    }
}

internal class PickMultipleImagesFromGallery : ActivityResultContract<Array<String>, List<Uri>>() {
    override fun createIntent(context: Context, input: Array<String>): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            if (input.size == 1) {
                type = input[0]
            } else {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, input)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()

        if (resultCode != android.app.Activity.RESULT_OK) {
            return uris
        }

        intent?.let {
            it.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i).uri?.let { uri ->
                        uris.add(uri)
                    }
                }
            }
            it.data?.let { uri ->
                if (uris.isEmpty()) {
                    uris.add(uri)
                }
            }
        }

        return uris
    }
}

@Composable
internal fun rememberGalleryOnlyPickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    allowedMimeTypes: Array<String> = arrayOf("image/*"),
    mimeTypeMismatchMessage: String? = null
): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val gallerySelectionErrorMsg = stringResource(Res.string.gallery_selection_error)
    val mimeTypeMismatchMsg = stringResource(Res.string.mime_type_mismatch_error)

    return rememberLauncherForActivityResult(
        contract = PickImageFromGallery()
    ) { uri: Uri? ->
        if (uri != null) {
            if (!uriMatchesMimeTypes(context, uri, allowedMimeTypes)) {
                val msg = mimeTypeMismatchMessage ?: mimeTypeMismatchMsg.format(allowedMimeTypes.joinToString(", "))
                onError(Exception(msg))
                return@rememberLauncherForActivityResult
            }
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val result = GalleryFileProcessor.processSelectedFile(context, uri, compressionLevel, includeExif)
                    if (result != null) {
                        onPhotoSelected(result)
                    } else {
                        onError(Exception(gallerySelectionErrorMsg))
                    }
                }
            } catch (e: Exception) {
                onError(e)
            }
        } else {
            onDismiss()
        }
    }
}

@Composable
internal fun rememberGalleryOnlyMultiplePickerLauncher(
    context: Context,
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    selectionLimit: Int = 10,
    allowedMimeTypes: Array<String> = arrayOf("image/*"),
    mimeTypeMismatchMessage: String? = null
): ManagedActivityResultLauncher<Array<String>, List<Uri>> {
    val gallerySelectionErrorMsg = stringResource(Res.string.gallery_selection_error)
    val mimeTypeMismatchMsg = stringResource(Res.string.mime_type_mismatch_error)

    return rememberLauncherForActivityResult(
        contract = PickMultipleImagesFromGallery()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val filteredUris = uris.filter { uri ->
                uriMatchesMimeTypes(context, uri, allowedMimeTypes)
            }

            if (filteredUris.isEmpty()) {
                val msg = mimeTypeMismatchMessage ?: mimeTypeMismatchMsg.format(allowedMimeTypes.joinToString(", "))
                onError(Exception(msg))
                return@rememberLauncherForActivityResult
            }

            val limitedUris = if (filteredUris.size > selectionLimit) {
                filteredUris.take(selectionLimit)
            } else {
                filteredUris
            }
            
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    val semaphore = Semaphore(3)
                    val results = mutableListOf<GalleryPhotoResult>()
                    val errors = mutableListOf<Exception>()

                    val deferredResults = limitedUris.map { uri ->
                        async(Dispatchers.IO) {
                            semaphore.withPermit {
                                try {
                                    GalleryFileProcessor.processSelectedFile(context, uri, compressionLevel, includeExif)
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
                        onError(Exception(gallerySelectionErrorMsg))
                    }
                }
            } catch (e: Exception) {
                onError(e)
            }
        } else {
            onDismiss()
        }
    }
}
