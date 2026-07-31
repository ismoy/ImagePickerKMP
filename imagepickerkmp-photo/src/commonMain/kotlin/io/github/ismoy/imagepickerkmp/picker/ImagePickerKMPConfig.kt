package io.github.ismoy.imagepickerkmp.picker

import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.config.PermissionAndConfirmationConfig

data class ImagePickerKMPConfig(
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig(),
    val cropConfig: CropConfig = CropConfig(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig()
)
