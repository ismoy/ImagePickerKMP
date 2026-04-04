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

    // rememberUpdatedState garantiza que el LaunchedEffect siempre llama a la versión
    // más reciente de los callbacks, incluso si el composable padre se recompuso mientras
    // el UIImagePickerController estaba abierto.
    val currentOnPhotosSelected by rememberUpdatedState(onPhotosSelected)
    val currentOnError by rememberUpdatedState(onError)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentOnCropPending by rememberUpdatedState(onCropPending)

    // Cuando el usuario cancela el crop: el Dialog ya está desmontado (showCropView=false)
    // y cropCancelled=true. SideEffect se ejecuta DESPUÉS de que la composición fue
    // aplicada al árbol de UI → onDismiss puede mutar activeMode sin crash en iOS.
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

    // Captura local inmutable: evita NullPointerException cuando el scope interno del Dialog
    // recompone mientras selectedPhotoForCrop ya fue puesto a null en onCancel.
    val photoForCrop = selectedPhotoForCrop
    if (showCropView && photoForCrop != null) {
        // Dialog crea una UIWindow propia en iOS que siempre está por encima
        // del Scaffold / NavHost del host — resuelve el problema de Z-order.
        Dialog(
            onDismissRequest = {
                // No permitir cerrar tocando fuera — el usuario debe usar
                // los botones Aceptar/Cancelar del crop view.
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
