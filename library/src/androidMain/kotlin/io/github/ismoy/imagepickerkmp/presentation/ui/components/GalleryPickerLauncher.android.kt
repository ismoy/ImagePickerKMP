package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeByteArray
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.scale
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
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
    val cameraCaptureConfig: CameraCaptureConfig?,
    val enableCrop: Boolean = false
)

@Suppress("ReturnCount","LongParameterList")
@Composable
actual fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String
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
        mimeTypes = mimeTypes.map { it.value },
        cameraCaptureConfig = cameraCaptureConfig,
        enableCrop = enableCrop
    )
    GalleryPickerLauncherContent(config)
}

@Composable
private fun GalleryPickerLauncherContent(config: GalleryPickerConfig) {
    var shouldLaunch by remember { mutableStateOf(false) }
    var selectedPhotoForConfirmation by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }

    val singlePickerLauncher = rememberSinglePickerLauncher(
        config.context,
        { photoResult ->
            if (config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView != null) {
                selectedPhotoForConfirmation = photoResult
            } else if (config.enableCrop) {
                selectedPhotoForConfirmation = photoResult
                showCropView = true
            } else {
                config.onPhotosSelected(listOf(photoResult))
            }
        },
        config.onError,
        config.onDismiss,
        config.cameraCaptureConfig?.compressionLevel
    )
    val multiplePickerLauncher = rememberMultiplePickerLauncher(
        config.context,
        config.onPhotosSelected,
        config.onError,
        config.onDismiss,
        config.cameraCaptureConfig?.compressionLevel
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
        if (showCropView) {
            ImageCropView(
                photoResult = PhotoResult(
                    uri = photoResult.uri,
                    width = photoResult.width,
                    height = photoResult.height,
                    fileName = photoResult.fileName,
                    fileSize = photoResult.fileSize
                ),
                cropConfig = CropConfig(
                    enabled = true,
                    circularCrop = false,
                    squareCrop = true
                ),
                onAccept = { croppedResult: PhotoResult ->
                    val galleryResult = GalleryPhotoResult(
                        uri = croppedResult.uri,
                        width = croppedResult.width,
                        height = croppedResult.height,
                        fileName = photoResult.fileName,
                        fileSize = photoResult.fileSize
                    )
                    config.onPhotosSelected(listOf(galleryResult))
                    selectedPhotoForConfirmation = null
                    showCropView = false
                },
                onCancel = {
                    selectedPhotoForConfirmation = null
                    showCropView = false
                    shouldLaunch = true
                }
            )
        } else {
            config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView?.invoke(
                PhotoResult(
                    uri = photoResult.uri,
                    width = photoResult.width,
                    height = photoResult.height,
                    fileName = photoResult.fileName,
                    fileSize = photoResult.fileSize
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
}

@Composable
private fun rememberSinglePickerLauncher(
    context: Context,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    if (uri != null) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                processSelectedImage(context, uri, onPhotoSelected, onError, compressionLevel)
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
    onDismiss: () -> Unit,
    compressionLevel: CompressionLevel? = null
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
) { uris: List<Uri> ->
    if (uris.isNotEmpty()) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val results = mutableListOf<GalleryPhotoResult>()
                for (uri in uris) {
                    try {
                        val result = processSelectedImageSuspend(context, uri, compressionLevel)
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
    uri: Uri,
    compressionLevel: CompressionLevel? = null
): GalleryPhotoResult? {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBytes = inputStream?.readBytes()
            inputStream?.close()

            if (originalBytes != null) {
                val fileName = getFileName(context, uri)
                
                if (compressionLevel != null) {
                    val bitmap = decodeByteArray(originalBytes, 0, originalBytes.size)
                    if (bitmap != null) {
                        val processedBitmap = processImageWithCompression(bitmap, compressionLevel)
                        val compressedBytes = compressBitmapToByteArray(processedBitmap, compressionLevel)
                        
                        val tempFile = createTempImageFile(context, compressedBytes)
                        if (tempFile != null) {
                            val fileSizeInKB = bytesToKB(compressedBytes.size.toLong())
                            logDebug("Compressed image - File size: ${fileSizeInKB}KB (${compressedBytes.size} bytes)")
                            return@withContext GalleryPhotoResult(
                                uri = Uri.fromFile(tempFile).toString(),
                                width = processedBitmap.width,
                                height = processedBitmap.height,
                                fileName = "compressed_$fileName",
                                fileSize = fileSizeInKB
                            )
                        }
                    }
                }
                
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                decodeByteArray(originalBytes, 0, originalBytes.size, options)
                val width = options.outWidth
                val height = options.outHeight
                val fileSizeInKB = bytesToKB(originalBytes.size.toLong())
                logDebug("Original image - File size: ${fileSizeInKB}KB (${originalBytes.size} bytes)")
                GalleryPhotoResult(
                    uri = uri.toString(),
                    width = width,
                    height = height,
                    fileName = fileName,
                    fileSize = fileSizeInKB
                )
            } else {
                null
            }
        } catch (e: Exception) {
            logDebug("Error processing selected image: ${e.message}")
            null
        }
    }
}

private suspend fun processSelectedImage(
    context: Context,
    uri: Uri,
    onPhotoSelected: (GalleryPhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    compressionLevel: CompressionLevel? = null
) {
    try {
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBytes = inputStream?.readBytes()
            inputStream?.close()

            if (originalBytes != null) {
                val fileName = getFileName(context, uri)
                
                if (compressionLevel != null) {
                    val bitmap = decodeByteArray(originalBytes, 0, originalBytes.size)
                    if (bitmap != null) {
                        val processedBitmap = processImageWithCompression(bitmap, compressionLevel)
                        val compressedBytes = compressBitmapToByteArray(processedBitmap, compressionLevel)
                        
                        val tempFile = createTempImageFile(context, compressedBytes)
                        if (tempFile != null) {
                            val fileSizeInKB = bytesToKB(compressedBytes.size.toLong())
                            logDebug("Compressed image processed - File size: ${fileSizeInKB}KB (${compressedBytes.size} bytes)")
                            onPhotoSelected(
                                GalleryPhotoResult(
                                    uri = Uri.fromFile(tempFile).toString(),
                                    width = processedBitmap.width,
                                    height = processedBitmap.height,
                                    fileName = "compressed_$fileName",
                                    fileSize = fileSizeInKB
                                )
                            )
                            return@withContext
                        }
                    }
                }
                
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                decodeByteArray(originalBytes, 0, originalBytes.size, options)
                val width = options.outWidth
                val height = options.outHeight
                val fileSizeInKB = bytesToKB(originalBytes.size.toLong())
                logDebug("Original image processed - File size: ${fileSizeInKB}KB (${originalBytes.size} bytes)")
                onPhotoSelected(
                    GalleryPhotoResult(
                        uri = uri.toString(),
                        width = width,
                        height = height,
                        fileName = fileName,
                        fileSize = fileSizeInKB
                    )
                )
            } else {
                onError(Exception(getStringResource(StringResource.GALLERY_SELECTION_ERROR)))
            }
        }
    } catch (e: Exception) {
        logDebug("Error processing selected image: ${e.message}")
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
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                result = it.getString(displayNameIndex)
            }
        }
    }
    return result
}

/**
 * Process bitmap with compression similar to ImageProcessor logic
 */
private fun processImageWithCompression(
    bitmap: Bitmap,
    compressionLevel: CompressionLevel
): Bitmap {
    val maxDimension = when (compressionLevel) {
        CompressionLevel.HIGH -> 1280
        CompressionLevel.MEDIUM -> 1920
        CompressionLevel.LOW -> 2560
    }
    
    val currentMaxDimension = maxOf(bitmap.width, bitmap.height)
    
    return if (currentMaxDimension > maxDimension) {
        val scale = maxDimension.toFloat() / currentMaxDimension.toFloat()
        val targetWidth = (bitmap.width * scale).toInt()
        val targetHeight = (bitmap.height * scale).toInt()
        
        val resizedBitmap = bitmap.scale(targetWidth, targetHeight)
        if (resizedBitmap != bitmap) {
            bitmap.recycle()
        }
        resizedBitmap
    } else {
        bitmap
    }
}

/**
 * Compress bitmap to byte array with quality based on compression level
 */
private fun compressBitmapToByteArray(
    bitmap: Bitmap,
    compressionLevel: CompressionLevel
): ByteArray {
    val quality = (compressionLevel.toQualityValue() * 100).toInt()
    val outputStream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}

/**
 * Create temporary file for compressed image
 */
private fun createTempImageFile(context: Context, imageBytes: ByteArray): java.io.File? {
    return try {
        val tempFile = java.io.File.createTempFile(
            "compressed_gallery_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        java.io.FileOutputStream(tempFile).use { outputStream ->
            outputStream.write(imageBytes)
        }
        tempFile
    } catch (e: Exception) {
        println("Error creating temp file: ${e.message}")
        null
    }
}

// Utility functions for better code practices
private fun bytesToKB(bytes: Long): Long = maxOf(1L, bytes / 1024)

private fun logDebug(message: String) {
    DefaultLogger.logDebug("📱 Android GalleryPickerLauncher: $message")
}
