package io.github.ismoy.imagepickerkmp.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.ismoy.imagepickerkmp.core.I18nKonfig.Errors.gallery_selection_error
import io.github.ismoy.imagepickerkmp.core.I18nKonfig.Errors.mime_type_mismatch_error
import io.github.ismoy.imagepickerkmp.gallery.GalleryFileProcessor
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

private class GetContentWithMimeTypes : androidx.activity.result.contract.ActivityResultContract<Array<String>, Uri?>() {
    override fun createIntent(context: Context, input: Array<String>): android.content.Intent {
        return android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
            addCategory(android.content.Intent.CATEGORY_OPENABLE)
            if (input.size == 1) {
                type = input[0]
            } else {
                type = "*/*"
                putExtra(android.content.Intent.EXTRA_MIME_TYPES, input)
            }
        }
    }
    override fun parseResult(resultCode: Int, intent: android.content.Intent?): Uri? {
        if (resultCode != android.app.Activity.RESULT_OK) return null
        return intent?.data
    }
}

private class GetMultipleContentsWithMimeTypes : androidx.activity.result.contract.ActivityResultContract<Array<String>, List<Uri>>() {
    override fun createIntent(context: Context, input: Array<String>): android.content.Intent {
        return android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply {
            addCategory(android.content.Intent.CATEGORY_OPENABLE)
            putExtra(android.content.Intent.EXTRA_ALLOW_MULTIPLE, true)
            if (input.size == 1) {
                type = input[0]
            } else {
                type = "*/*"
                putExtra(android.content.Intent.EXTRA_MIME_TYPES, input)
            }
        }
    }
    override fun parseResult(resultCode: Int, intent: android.content.Intent?): List<Uri> {
        val uris = mutableListOf<Uri>()
        if (resultCode != android.app.Activity.RESULT_OK) return uris
        intent?.let {
            it.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    clipData.getItemAt(i).uri?.let { uri -> uris.add(uri) }
                }
            }
            it.data?.let { uri -> if (uris.isEmpty()) uris.add(uri) }
        }
        return uris
    }
}

@Composable
internal fun rememberSinglePickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    allowedMimeTypes: Array<String> = arrayOf("image/*"),
    mimeTypeMismatchMessage: String? = null
): ManagedActivityResultLauncher<Array<String>, Uri?> {
    val gallerySelectionErrorMsg = gallery_selection_error
    val mimeTypeMismatchMsg = mime_type_mismatch_error
    val composableScope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(
        contract = GetContentWithMimeTypes()
    ) { uri: Uri? ->
        if (uri != null) {
            if (!uriMatchesMimeTypes(context, uri, allowedMimeTypes)) {
                val msg = mimeTypeMismatchMessage ?: mimeTypeMismatchMsg.format(allowedMimeTypes.joinToString(", "))
                onError(Exception(msg))
                return@rememberLauncherForActivityResult
            }
            try {
                composableScope.launch {
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
internal fun rememberMultiplePickerLauncher(
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
    val mimeTypeMismatchMsg = mime_type_mismatch_error
    val composableScope = rememberCoroutineScope()

    return rememberLauncherForActivityResult(
        contract = GetMultipleContentsWithMimeTypes()
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
                composableScope.launch {
                    val semaphore = Semaphore(3)
                    val results = mutableListOf<GalleryPhotoResult>()
                    val errors = mutableListOf<Exception>()

                    val deferredResults = limitedUris.map { uri ->
                        async {
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

                    results.addAll(deferredResults.awaitAll().filterNotNull())

                    if (errors.isNotEmpty()) {
                        errors.forEach { onError(it) }
                    }

                    if (results.isNotEmpty()) {
                        onPhotosSelected(results)
                    } else {
                        onError(Exception(gallery_selection_error))
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
