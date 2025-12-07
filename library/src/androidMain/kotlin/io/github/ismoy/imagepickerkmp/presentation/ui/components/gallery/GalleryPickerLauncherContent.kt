package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageCropView


@Composable
 fun GalleryPickerLauncherContent(config: GalleryPickerConfig) {
    var shouldLaunch by remember { mutableStateOf(false) }
    var selectedPhotoForConfirmation by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    
    val shouldShowCrop = config.cameraCaptureConfig?.cropConfig?.enabled == true || 
                        (config.enableCrop && config.cameraCaptureConfig?.cropConfig?.enabled != false)

    val effectiveGalleryConfig = remember(config.mimeTypes) { 
        config.getEffectiveAndroidGalleryConfig() 
    }

    val singlePickerLauncher = if (effectiveGalleryConfig.forceGalleryOnly) {
        rememberGalleryOnlyPickerLauncher(
            config.context,
            { photoResult ->
                if (config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView != null) {
                    selectedPhotoForConfirmation = photoResult
                } else if (shouldShowCrop) {
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
    } else {
        rememberSinglePickerLauncher(
            config.context,
            { photoResult ->
                if (config.cameraCaptureConfig?.permissionAndConfirmationConfig?.customConfirmationView != null) {
                    selectedPhotoForConfirmation = photoResult
                } else if (shouldShowCrop) {
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
    }
    
    val multiplePickerLauncher = if (effectiveGalleryConfig.forceGalleryOnly) {
        rememberGalleryOnlyMultiplePickerLauncher(
            config.context,
            config.onPhotosSelected,
            config.onError,
            config.onDismiss,
            config.cameraCaptureConfig?.compressionLevel,
            config.includeExif
        )
    } else {
        rememberMultiplePickerLauncher(
            config.context,
            config.onPhotosSelected,
            config.onError,
            config.onDismiss,
            config.cameraCaptureConfig?.compressionLevel,
            config.includeExif
        )
    }

    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            try {
                val mimeType = if (effectiveGalleryConfig.forceGalleryOnly) {
                    "image/*"
                } else {
                    when {
                        config.mimeTypes.size > 1 -> "*/*"
                        config.mimeTypes.any { it.contains("application/pdf", ignoreCase = true) } -> "*/*"
                        else -> config.mimeTypes.firstOrNull() ?: "image/*"
                    }
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
                cropConfig = if (config.cameraCaptureConfig?.cropConfig?.enabled == true) {
                    config.cameraCaptureConfig.cropConfig
                } else {
                    CropConfig(
                        enabled = true,
                        circularCrop = true,
                        squareCrop = true
                    )
                },
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
