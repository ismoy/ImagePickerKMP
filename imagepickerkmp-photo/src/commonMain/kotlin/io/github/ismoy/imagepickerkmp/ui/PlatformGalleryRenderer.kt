package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType

@Suppress("LongParameterList")
@Composable
internal expect fun PlatformGalleryRenderer(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    selectionLimit: Long = 30L,
    cameraCaptureConfig: CameraCaptureConfig? = null,
    enableCrop: Boolean = false,
    fileFilterDescription: String = "Image files",
    includeExif: Boolean = false,
    mimeTypeMismatchMessage: String? = null,
    compressionLevel: CompressionLevel? = null,
    onCropPending: () -> Unit = {}
)
