package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    includeExif: Boolean
) {
    var selectedPhotoForCrop by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        val compressionLevel = cameraCaptureConfig?.compressionLevel
        
        if (allowMultiple) {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = true,
                selectionLimit = selectionLimit,
                compressionLevel = compressionLevel,
                includeExif = includeExif,
                onPhotosSelected = { results ->
                    if (enableCrop && results.size == 1) {
                        selectedPhotoForCrop = results.first()
                        showCropView = true
                    } else {
                        onPhotosSelected(results)
                    }
                }
            )
        } else {
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result -> 
                    if (enableCrop) {
                        selectedPhotoForCrop = result
                        showCropView = true
                    } else {
                        onPhotosSelected(listOf(result))
                    }
                },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = false,
                selectionLimit = 1,
                compressionLevel = compressionLevel,
                includeExif = includeExif
            )
        }
    }
    
    if (showCropView && selectedPhotoForCrop != null) {
        ImageCropView(
            photoResult = PhotoResult(
                uri = selectedPhotoForCrop!!.uri,
                width = selectedPhotoForCrop!!.width,
                height = selectedPhotoForCrop!!.height,
                fileName = selectedPhotoForCrop!!.fileName,
                fileSize = selectedPhotoForCrop!!.fileSize,
                mimeType = selectedPhotoForCrop!!.mimeType,
                exif = selectedPhotoForCrop!!.exif
            ),
            cropConfig = cameraCaptureConfig?.cropConfig ?: CropConfig(
                enabled = true,
                circularCrop = false,
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
                onPhotosSelected(listOf(croppedGalleryResult))
                showCropView = false
                selectedPhotoForCrop = null
            },
            onCancel = {
                showCropView = false
                selectedPhotoForCrop = null
                onDismiss()
            }
        )
    }
}
