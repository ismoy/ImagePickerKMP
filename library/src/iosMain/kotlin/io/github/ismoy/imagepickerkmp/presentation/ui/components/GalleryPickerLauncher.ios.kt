package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.data.orchestrators.GalleryPickerOrchestrator
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

@Suppress("LongParameterList")
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
    includeExif: Boolean,
    mimeTypeMismatchMessage: String?,
    onCropPending: () -> Unit
) {
    var selectedPhotoForCrop by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    var cropCancelled by remember { mutableStateOf(false) }

    // rememberUpdatedState ensures LaunchedEffect always calls the latest version
    // of the callbacks, even if the parent composable recomposed while
    // the UIImagePickerController was open.
    val currentOnPhotosSelected by rememberUpdatedState(onPhotosSelected)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnCropPending by rememberUpdatedState(onCropPending)

    // When the user cancels the crop: the Dialog is already unmounted (showCropView=false)
    // and cropCancelled=true. SideEffect runs AFTER the composition was
    // applied to the UI tree → onDismiss can mutate activeMode without crashing on iOS.
    if (cropCancelled && !showCropView) {
        SideEffect {
            cropCancelled = false
            currentOnDismiss()
        }
    }

    LaunchedEffect(Unit) {
        val compressionLevel = cameraCaptureConfig?.compressionLevel

        if (allowMultiple) {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { _ -> },
                onError = { currentOnError(it) },
                onDismiss = { currentOnDismiss() },
                allowMultiple = true,
                selectionLimit = selectionLimit,
                compressionLevel = compressionLevel,
                includeExif = includeExif,
                mimeTypes = mimeTypes,
                mimeTypeMismatchMessage = mimeTypeMismatchMessage,
                onPhotosSelected = { results ->
                    if (enableCrop && results.size == 1) {
                        currentOnCropPending()
                        selectedPhotoForCrop = results.first()
                        showCropView = true
                    } else {
                        currentOnPhotosSelected(results)
                    }
                }
            )
        } else {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                    if (enableCrop) {
                        currentOnCropPending()
                        selectedPhotoForCrop = result
                        showCropView = true
                    } else {
                        currentOnPhotosSelected(listOf(result))
                    }
                },
                onError = { currentOnError(it) },
                onDismiss = { currentOnDismiss() },
                allowMultiple = false,
                selectionLimit = 1,
                compressionLevel = compressionLevel,
                includeExif = includeExif,
                mimeTypes = mimeTypes,
                mimeTypeMismatchMessage = mimeTypeMismatchMessage
            )
        }
    }

    // Local immutable capture: prevents NullPointerException when the Dialog's inner scope
    // recomposes while selectedPhotoForCrop was already set to null in onCancel.
    val photoForCrop = selectedPhotoForCrop
    if (showCropView && photoForCrop != null) {
        // Dialog creates its own UIWindow on iOS, always on top
        // of the host Scaffold / NavHost — resolves the Z-order issue.
        Dialog(
            onDismissRequest = {
                // Do not allow closing by tapping outside — the user must use
                // the Accept/Cancel buttons of the crop view.
            },
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
                onAccept = { croppedResult ->
                    val croppedGalleryResult = GalleryPhotoResult(
                        uri = croppedResult.uri,
                        width = croppedResult.width,
                        height = croppedResult.height,
                        fileName = croppedResult.fileName,
                        fileSize = croppedResult.fileSize,
                        mimeType = croppedResult.mimeType,
                        exif = croppedResult.exif
                    )
                    currentOnPhotosSelected(listOf(croppedGalleryResult))
                    showCropView = false
                    selectedPhotoForCrop = null
                },
                onCancel = {
                    showCropView = false
                    selectedPhotoForCrop = null
                    cropCancelled = true
                }
            )
        }
    }
}
