package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.gallery.GalleryPickerOrchestrator
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

@Composable
internal fun rememberGalleryPickerState(
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
    compressionLevel: CompressionLevel?,
    onCropPending: () -> Unit
): GalleryPickerState {
    return remember { 
        GalleryPickerState(
            onPhotosSelected, onError, onDismiss, allowMultiple, mimeTypes, 
            selectionLimit, cameraCaptureConfig, enableCrop, fileFilterDescription, 
            includeExif, mimeTypeMismatchMessage, compressionLevel, onCropPending
        )
    }
}

@Suppress("LongParameterList")
@Stable
internal class GalleryPickerState(
    val onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit,
    val allowMultiple: Boolean,
    val mimeTypes: List<MimeType>,
    val selectionLimit: Long,
    val cameraCaptureConfig: CameraCaptureConfig?,
    val enableCrop: Boolean,
    val fileFilterDescription: String,
    val includeExif: Boolean,
    val mimeTypeMismatchMessage: String?,
    val compressionLevel: CompressionLevel?,
    val onCropPending: () -> Unit
) {
    var selectedPhotoForCrop by mutableStateOf<GalleryPhotoResult?>(null)
    var showCropView by mutableStateOf(false)
    var cropCancelled by mutableStateOf(false)

    fun onCropCancelledEffectHandled(currentOnDismiss: () -> Unit) {
        cropCancelled = false
        currentOnDismiss()
    }

    fun launchGalleryFlow(
        currentOnPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
        currentOnError: (Exception) -> Unit,
        currentOnDismiss: () -> Unit,
        currentOnCropPending: () -> Unit
    ) {
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

    fun acceptCrop(croppedResult: PhotoResult, currentOnPhotosSelected: (List<GalleryPhotoResult>) -> Unit) {
        val originalPhoto = selectedPhotoForCrop
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
    }

    fun cancelCrop() {
        showCropView = false
        selectedPhotoForCrop = null
        cropCancelled = true
    }
}
