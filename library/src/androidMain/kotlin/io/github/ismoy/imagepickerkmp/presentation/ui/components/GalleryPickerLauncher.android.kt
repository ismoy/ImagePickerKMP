package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery.rememberSinglePickerLauncher
import io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery.rememberMultiplePickerLauncher

private data class GalleryPickerConfig(
    val context: Context,
    val onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit,
    val allowMultiple: Boolean,
    val mimeTypes: List<String>,
    val cameraCaptureConfig: CameraCaptureConfig?,
    val enableCrop: Boolean = false,
    val includeExif: Boolean = false
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
    fileFilterDescription: String,
    includeExif: Boolean
) {
    val context = LocalContext.current
    val activity = context
    if (activity !is ComponentActivity) {
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
        enableCrop = enableCrop,
        includeExif = includeExif
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
        config.cameraCaptureConfig?.compressionLevel,
        config.includeExif
    )
    val multiplePickerLauncher = rememberMultiplePickerLauncher(
        config.context,
        config.onPhotosSelected,
        config.onError,
        config.onDismiss,
        config.cameraCaptureConfig?.compressionLevel,
        config.includeExif
    )

    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            try {
                val mimeType = when {
                    config.mimeTypes.size > 1 -> "*/*"
                    config.mimeTypes.any { it.contains("application/pdf", ignoreCase = true) } -> "*/*"
                    else -> config.mimeTypes.firstOrNull() ?: "image/*"
                }
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
                    fileSize = photoResult.fileSize,
                    mimeType = photoResult.mimeType,
                    exif = photoResult.exif
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
                        fileSize = photoResult.fileSize,
                        mimeType = photoResult.mimeType,
                        exif = photoResult.exif
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
                    fileSize = photoResult.fileSize,
                    mimeType = photoResult.mimeType,
                    exif = photoResult.exif
                ),
                { confirmedResult ->
                    val galleryResult = GalleryPhotoResult(
                        uri = confirmedResult.uri,
                        width = confirmedResult.width,
                        height = confirmedResult.height,
                        fileName = photoResult.fileName,
                        fileSize = photoResult.fileSize,
                        mimeType = photoResult.mimeType,
                        exif = photoResult.exif
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
