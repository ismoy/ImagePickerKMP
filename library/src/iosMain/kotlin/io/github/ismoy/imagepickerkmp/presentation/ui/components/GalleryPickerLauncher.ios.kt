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
    enableCrop: Boolean
) {
    var selectedPhotoForCrop by remember { mutableStateOf<GalleryPhotoResult?>(null) }
    var showCropView by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        val compressionLevel = cameraCaptureConfig?.compressionLevel
        
        if (allowMultiple) {
            val selectedImages = mutableListOf<GalleryPhotoResult>()
            GalleryPickerOrchestrator.launchGallery(
                onPhotoSelected = { result ->
                    if (enableCrop && selectedImages.isEmpty()) {
                        selectedPhotoForCrop = result
                        showCropView = true
                    } else {
                        selectedImages.add(result)
                        onPhotosSelected(selectedImages.toList())
                    }
                },
                onError = onError,
                onDismiss = onDismiss,
                allowMultiple = true,
                selectionLimit = selectionLimit,
                compressionLevel = compressionLevel
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
                compressionLevel = compressionLevel
            )
        }
    }
    
    if (showCropView && selectedPhotoForCrop != null) {
        val cropConfig = CropConfig(
            enabled = true,
            circularCrop = false,
            squareCrop = true,
            freeformCrop = true
        )
        
        ImageCropView(
            photoResult = PhotoResult(
                uri = selectedPhotoForCrop!!.uri,
                width = selectedPhotoForCrop!!.width,
                height = selectedPhotoForCrop!!.height,
                fileName = selectedPhotoForCrop!!.fileName,
                fileSize = selectedPhotoForCrop!!.fileSize
            ),
            cropConfig = cropConfig,
            onAccept = { croppedResult ->
                val croppedGalleryResult = GalleryPhotoResult(
                    uri = croppedResult.uri,
                    width = croppedResult.width,
                    height = croppedResult.height,
                    fileName = croppedResult.fileName,
                    fileSize = croppedResult.fileSize
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
