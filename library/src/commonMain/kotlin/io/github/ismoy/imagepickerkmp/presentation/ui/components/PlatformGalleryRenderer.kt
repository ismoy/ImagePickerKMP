package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * Internal composable that launches the platform-specific gallery picker.
 *
 * This is an implementation detail used by
 * [rememberImagePickerKMP][io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP].
 * It is not part of the public API.
 */
@Suppress("LongParameterList")
@Composable
internal expect fun PlatformGalleryRenderer(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    selectionLimit: Long = SELECTION_LIMIT,
    cameraCaptureConfig: CameraCaptureConfig? = null,
    enableCrop: Boolean = false,
    fileFilterDescription: String = "Image files",
    includeExif: Boolean = false,
    mimeTypeMismatchMessage: String? = null,
    onCropPending: () -> Unit = {}
)
