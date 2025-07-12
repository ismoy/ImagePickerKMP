package io.github.ismoy.imagepickerkmp

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler.PhotoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun GalleryPickerLauncher(
    context: Any?,
    onPhotosSelected: (List<PhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<String>
) {
    if (context !is ComponentActivity) {
        onError(Exception(getStringResource(StringResource.INVALID_CONTEXT_ERROR)))
        return
    }

    var shouldLaunch by remember { mutableStateOf(false) }

    val singlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                    processSelectedImage(context, uri, {
                        onPhotosSelected(listOf(it))
                    }, onError)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    val multiplePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            try {
                kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                    val results = mutableListOf<PhotoResult>()
                    for (uri in uris) {
                        try {
                            val result = processSelectedImageSuspend(context, uri)
                            if (result != null) results.add(result)
                        } catch (e: Exception) {
                            // Si una imagen falla, continúa con las demás
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
        }
    }

    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            try {
                val mimeType = mimeTypes.firstOrNull() ?: "image/*"
                if (allowMultiple) {
                    multiplePickerLauncher.launch(mimeType)
                } else {
                    singlePickerLauncher.launch(mimeType)
                }
                shouldLaunch = false
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    LaunchedEffect(Unit) {
        shouldLaunch = true
    }
}

private suspend fun processSelectedImageSuspend(
    context: Context,
    uri: Uri
): PhotoResult? {
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
                PhotoResult(
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
            null
        }
    }
}

private suspend fun processSelectedImage(
    context: Context,
    uri: Uri,
    onPhotoSelected: (PhotoResult) -> Unit,
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
                    PhotoResult(
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
    return try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    it.getString(displayNameIndex)
                } else null
            } else null
        }
    } catch (e: Exception) {
        null
    }
} 