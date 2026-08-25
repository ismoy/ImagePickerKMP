package io.github.ismoy.imagepickerkmp.ui

import android.content.ActivityNotFoundException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.I18nKonfig.Errors.gallery_unavailable_error
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.PhotoCaptureException
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

@Composable
internal fun rememberGalleryPickerState(
    config: GalleryPickerConfig
): GalleryPickerState {
    return remember(config) { GalleryPickerState(config) }
}

@Stable
internal class GalleryPickerState(
    val config: GalleryPickerConfig
) {
    var shouldLaunch by mutableStateOf(false)
    var selectedPhotoForCrop by mutableStateOf<GalleryPhotoResult?>(null)
    var showCropView by mutableStateOf(false)

    val shouldShowCrop: Boolean
        get() = config.enableCrop || config.cameraCaptureConfig?.cropConfig?.enabled == true

    fun onPhotoSelected(photoResult: GalleryPhotoResult) {
        if (shouldShowCrop) {
            selectedPhotoForCrop = photoResult
            showCropView = true
        } else {
            config.onPhotosSelected(listOf(photoResult))
        }
    }

    fun onLaunchEffectHandled(multiplePickerLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, List<android.net.Uri>>, singlePickerLauncher: androidx.activity.compose.ManagedActivityResultLauncher<Array<String>, android.net.Uri?>, mimeTypesArray: Array<String>) {
        if (shouldLaunch) {
            shouldLaunch = false
            try {
                if (config.allowMultiple) {
                    multiplePickerLauncher.launch(mimeTypesArray)
                } else {
                    singlePickerLauncher.launch(mimeTypesArray)
                }
            } catch (e: ActivityNotFoundException) {
                reportLaunchFailure(PhotoCaptureException(gallery_unavailable_error, e), config.onError, config.onDismiss)
            } catch (e: Exception) {
                reportLaunchFailure(e, config.onError, config.onDismiss)
            }
        }
    }

    fun setShouldLaunch() {
        shouldLaunch = true
    }

    fun acceptCrop(croppedResult: PhotoResult) {
        val originalPhoto = selectedPhotoForCrop
        val finalPhoto = GalleryPhotoResult(
            uri = croppedResult.uri,
            width = croppedResult.width,
            height = croppedResult.height,
            fileName = croppedResult.fileName,
            fileSize = croppedResult.fileSize,
            mimeType = croppedResult.mimeType,
            exif = originalPhoto?.exif
        )
        config.onPhotosSelected(listOf(finalPhoto))
        selectedPhotoForCrop = null
        showCropView = false
    }

    fun cancelCrop() {
        selectedPhotoForCrop = null
        showCropView = false
        config.onDismiss()
    }
}
