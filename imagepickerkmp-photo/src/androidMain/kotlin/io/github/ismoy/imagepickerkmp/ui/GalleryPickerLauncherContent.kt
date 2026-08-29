package io.github.ismoy.imagepickerkmp.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.NUMBER_TEN
import io.github.ismoy.imagepickerkmp.gallery.AndroidGalleryConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

internal data class GalleryPickerConfig(
    val context: Context,
    val onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit,
    val allowMultiple: Boolean,
    val mimeTypes: List<String>,
    val cameraCaptureConfig: CameraCaptureConfig?,
    val enableCrop: Boolean = false,
    val includeExif: Boolean = false,
    val selectionLimit: Int = NUMBER_TEN,
    val compressionLevel: CompressionLevel? = null,
    val androidGalleryConfig: AndroidGalleryConfig? = null,
    val mimeTypeMismatchMessage: String? = null
) {
    internal fun getEffectiveAndroidGalleryConfig(): AndroidGalleryConfig {
        return androidGalleryConfig ?: AndroidGalleryConfig.forMimeTypeStrings(mimeTypes)
    }
}

@Composable
internal fun GalleryPickerLauncherContent(config: GalleryPickerConfig) {
    val state = rememberGalleryPickerState(config)

    val effectiveGalleryConfig = remember(config.mimeTypes) {
        config.getEffectiveAndroidGalleryConfig()
    }

    val mimeTypesArray: Array<String> = remember(config.mimeTypes) {
        if (config.mimeTypes.isEmpty()) arrayOf("image/*") else config.mimeTypes.toTypedArray()
    }

    val singlePickerLauncher = if (effectiveGalleryConfig.forceGalleryOnly) {
        rememberGalleryOnlyPickerLauncher(
            config.context, { state.onPhotoSelected(it) }, config.onError, config.onDismiss,
            config.compressionLevel, config.includeExif,
            mimeTypesArray, config.mimeTypeMismatchMessage
        )
    } else {
        rememberSinglePickerLauncher(
            config.context, { state.onPhotoSelected(it) }, config.onError, config.onDismiss,
            config.compressionLevel, config.includeExif,
            mimeTypesArray, config.mimeTypeMismatchMessage
        )
    }

    val multiplePickerLauncher = if (effectiveGalleryConfig.forceGalleryOnly) {
        rememberGalleryOnlyMultiplePickerLauncher(
            config.context, config.onPhotosSelected, config.onError, config.onDismiss,
            config.compressionLevel, config.includeExif,
            config.selectionLimit, mimeTypesArray, config.mimeTypeMismatchMessage
        )
    } else {
        rememberMultiplePickerLauncher(
            config.context, config.onPhotosSelected, config.onError, config.onDismiss,
            config.compressionLevel, config.includeExif,
            config.selectionLimit, mimeTypesArray, config.mimeTypeMismatchMessage
        )
    }

    LaunchedEffect(state.shouldLaunch) {
        state.onLaunchEffectHandled(multiplePickerLauncher, singlePickerLauncher, mimeTypesArray)
    }

    LaunchedEffect(Unit) {
        state.setShouldLaunch()
    }

    if (state.showCropView && state.selectedPhotoForCrop != null) {
        val photoForCrop = state.selectedPhotoForCrop!!
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            ImageCropView(
                photoResult = PhotoResult(
                    uri = photoForCrop.uri,
                    width = photoForCrop.width,
                    height = photoForCrop.height,
                    fileName = photoForCrop.fileName,
                    fileSize = photoForCrop.fileSize,
                    mimeType = photoForCrop.mimeType,
                    exif = photoForCrop.exif
                ),
                cropConfig = if (config.cameraCaptureConfig?.cropConfig?.enabled == true) {
                    config.cameraCaptureConfig.cropConfig
                } else {
                    CropConfig(enabled = true)
                },
                onAccept = { state.acceptCrop(it) },
                onCancel = { state.cancelCrop() },
                onSkip = { state.skipCrop() }
            )
        }
    }
}
