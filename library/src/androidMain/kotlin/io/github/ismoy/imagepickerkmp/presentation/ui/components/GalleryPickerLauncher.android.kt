package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class GalleryPickerConfig(
    val context: ComponentActivity,
    val onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit,
    val allowMultiple: Boolean,
    val mimeTypes: List<String>,
    val cameraCaptureConfig: CameraCaptureConfig?
)

@Suppress("ReturnCount","LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<String>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?
) {
    val context = LocalContext.current
    if (context !is ComponentActivity) {
        onError(Exception(getStringResource(StringResource.INVALID_CONTEXT_ERROR)))
        return
    }

    val config = GalleryPickerConfig(
        context = context,
        onPhotosSelected = onPhotosSelected,
        onError = onError,
        onDismiss = onDismiss,
        allowMultiple = allowMultiple,
        mimeTypes = mimeTypes,
        cameraCaptureConfig = cameraCaptureConfig
    )
    GalleryPickerLauncherContent(config)
}

@Composable
private fun GalleryPickerLauncherContent(config: GalleryPickerConfig) {
    var shouldLaunch by remember { mutableStateOf(false) }
    var selectedPhotoForConfirmation by remember { mutableStateOf<GalleryPhotoResult?>(null) }

    val singlePickerLauncher = rememberSinglePickerLauncher(
        config.context,
        { photoResult ->
            if (config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView != null) {
                selectedPhotoForConfirmation = photoResult
            } else {
                config.onPhotosSelected(listOf(photoResult))
            }
        },
        config.onError,
        config.onDismiss
    )
    val multiplePickerLauncher = rememberMultiplePickerLauncher(
        config.context,
        config.onPhotosSelected,
        config.onError,
        config.onDismiss
    )

    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            try {
                val mimeType = config.mimeTypes.firstOrNull() ?: "image/*"
                if (config.allowMultiple) {
                    multiplePickerLauncher.launch(mimeType)
                } else {
                    singlePickerLauncher.launch(mimeType)
                }
                shouldLaunch = false
            } catch (e: Exception) {
                config.onError(e)
            }
        }
    }

    LaunchedEffect(Unit) {
        shouldLaunch = true
    }

    selectedPhotoForConfirmation?.let { photoResult ->
        config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView?.invoke(
            PhotoResult(
                uri = photoResult.uri,
                width = photoResult.width,
                height = photoResult.height
            ),
            { confirmedResult ->
                val galleryResult = GalleryPhotoResult(
                    uri = confirmedResult.uri,
                    width = confirmedResult.width,
                    height = confirmedResult.height,
                    fileName = photoResult.fileName,
                    fileSize = photoResult.fileSize
                )
                config.onPhotosSelected(listOf(galleryResult))
                selectedPhotoForConfirmation = null
            },
            {
                selectedPhotoForConfirmation = null
                shouldLaunch = true
            }
        )
    }
}

@Composable
private fun rememberSinglePickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    if (uri != null) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                processSelectedImage(context, uri, onPhotoSelected, onError)
            }
        } catch (e: Exception) {
            onError(e)
        }
    } else {
        onDismiss()
    }
}

@Composable
private fun rememberMultiplePickerLauncher(
    context: Context,
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
) { uris: List<Uri> ->
    if (uris.isNotEmpty()) {
        try {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                val results = mutableListOf<GalleryPhotoResult>()
                for (uri in uris) {
                    try {
                        val result = processSelectedImageSuspend(context, uri)
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

private suspend fun processSelectedImageSuspend(
    context: Context,
    uri: Uri
): GalleryPhotoResult? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val fileName = getFileName(context, uri)
                val fileSize = bytes.size.toLong()
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                val width = options.outWidth
                val height = options.outHeight
                GalleryPhotoResult(
                    uri = uri.toString(),
                    width = width,
                    height = height,
                    fileName = fileName,
                    fileSize = fileSize
                )
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error processing selected image: \\${e.message}")
            null
        }
    }
}

private suspend fun processSelectedImage(
    context: Context,
    uri: Uri,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val fileName = getFileName(context, uri)
                val fileSize = bytes.size.toLong()
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                val width = options.outWidth
                val height = options.outHeight
                onPhotoSelected(
                    GalleryPhotoResult(
                        uri = uri.toString(),
                        width = width,
                        height = height,
                        fileName = fileName,
                        fileSize = fileSize
                    )
                )
            } else {
                onError(Exception(getStringResource(StringResource.GALLERY_SELECTION_ERROR)))
            }
        }
    } catch (e: Exception) {
        onError(e)
    }
}

private fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null
    val cursor = try {
        context.contentResolver.query(uri, null, null, null, null)
    } catch (e: Exception) {
        println("Error querying file name: \\${e.message}")
        null
    }
    cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                result = it.getString(displayNameIndex)
            }
        }
    }
    return result
}
