package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

@Suppress("LongParameterList")
@Composable
internal actual fun PlatformGalleryRenderer(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String,
    includeExif: Boolean,
    mimeTypeMismatchMessage: String?,
    onCropPending: () -> Unit
) {
    val state = rememberGalleryPickerState(
        onPhotosSelected, onError, onDismiss, allowMultiple, mimeTypes,
        selectionLimit, cameraCaptureConfig, enableCrop, fileFilterDescription,
        includeExif, mimeTypeMismatchMessage, onCropPending
    )

    val currentOnPhotosSelected by rememberUpdatedState(onPhotosSelected)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnCropPending by rememberUpdatedState(onCropPending)

    if (state.cropCancelled && !state.showCropView) {
        SideEffect {
            state.onCropCancelledEffectHandled(currentOnDismiss)
        }
    }

    LaunchedEffect(Unit) {
        state.launchGalleryFlow(
            currentOnPhotosSelected,
            currentOnError,
            currentOnDismiss,
            currentOnCropPending
        )
    }

    if (state.showCropView && state.selectedPhotoForCrop != null) {
        val photoForCrop = state.selectedPhotoForCrop!!
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
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
                cropConfig = cameraCaptureConfig?.cropConfig ?: CropConfig(
                    enabled = true,
                    circularCrop = true,
                    squareCrop = true,
                    freeformCrop = true
                ),
                onAccept = { state.acceptCrop(it, currentOnPhotosSelected) },
                onCancel = { state.cancelCrop() }
            )
        }
    }
}
