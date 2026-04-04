package io.github.ismoy.imagepickerkmp.presentation.ui.components.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.data.models.GalleryPickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImageCropView


@Composable
internal fun GalleryPickerLauncherContent(config: GalleryPickerConfig) {
    var shouldLaunch by remember { mutableStateOf(false) }
    var selectedPhotoForConfirmation by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }

    // Crop is enabled if explicitly set via enableCrop=true OR via cameraCaptureConfig.cropConfig.enabled=true
    // Evaluate once and keep stable — avoids race conditions on Android 16
    val shouldShowCrop = remember(config.enableCrop, config.cameraCaptureConfig) {
        config.enableCrop || config.cameraCaptureConfig?.cropConfig?.enabled == true
    }

    val effectiveGalleryConfig = remember(config.mimeTypes) { 
        config.getEffectiveAndroidGalleryConfig() 
    }

    val mimeTypesArray: Array<String> = remember(config.mimeTypes) {
        if (config.mimeTypes.isEmpty()) arrayOf("image/*") else config.mimeTypes.toTypedArray()
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
            config.includeExif,
            mimeTypesArray,
            config.mimeTypeMismatchMessage
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
            config.includeExif,
            mimeTypesArray,
            config.mimeTypeMismatchMessage
        )
    }
    
    val multiplePickerLauncher = if (effectiveGalleryConfig.forceGalleryOnly) {
        rememberGalleryOnlyMultiplePickerLauncher(
            config.context,
            config.onPhotosSelected,
            config.onError,
            config.onDismiss,
            config.cameraCaptureConfig?.compressionLevel,
            config.includeExif,
            config.selectionLimit,
            mimeTypesArray,
            config.mimeTypeMismatchMessage
        )
    } else {
        rememberMultiplePickerLauncher(
            config.context,
            config.onPhotosSelected,
            config.onError,
            config.onDismiss,
            config.cameraCaptureConfig?.compressionLevel,
            config.includeExif,
            config.selectionLimit,
            mimeTypesArray,
            config.mimeTypeMismatchMessage
        )
    }

    LaunchedEffect(shouldLaunch) {
        if (shouldLaunch) {
            try {
                if (config.allowMultiple) {
                    multiplePickerLauncher.launch(mimeTypesArray)
                } else {
                    singlePickerLauncher.launch(mimeTypesArray)
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
            Dialog(
                onDismissRequest = {
                    selectedPhotoForConfirmation = null
                    showCropView = false
                    config.onDismiss()
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
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
                    // Fallback: crop habilitado sin cropConfig personalizado — usar defaults mínimos
                    CropConfig(enabled = true)
                },
                onAccept = { croppedResult: PhotoResult ->
                    val galleryResult = GalleryPhotoResult(
                        uri = croppedResult.uri,
                        width = croppedResult.width,
                        height = croppedResult.height,
                        fileName = croppedResult.fileName,
                        fileSize = croppedResult.fileSize,
                        mimeType = croppedResult.mimeType,
                        exif = photoResult.exif
                    )
                    config.onPhotosSelected(listOf(galleryResult))
                    selectedPhotoForConfirmation = null
                    showCropView = false
                },
                onCancel = {
                    selectedPhotoForConfirmation = null
                    showCropView = false
                    config.onDismiss()
                }
                )  // ImageCropView
            }  // Dialog
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
                        fileName = confirmedResult.fileName,
                        fileSize = confirmedResult.fileSize,
                        mimeType = confirmedResult.mimeType,
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
