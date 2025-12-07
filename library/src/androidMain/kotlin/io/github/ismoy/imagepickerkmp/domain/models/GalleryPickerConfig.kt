package io.github.ismoy.imagepickerkmp.domain.models

import android.content.Context
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.AndroidGalleryConfig

 data class GalleryPickerConfig(
    val context: Context,
    val onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit,
    val allowMultiple: Boolean,
    val mimeTypes: List<String>,
    val cameraCaptureConfig: CameraCaptureConfig?,
    val enableCrop: Boolean = false,
    val includeExif: Boolean = false,
    val androidGalleryConfig: AndroidGalleryConfig? = null
 ) {
    /**
     * Gets the effective AndroidGalleryConfig, using automatic detection if not explicitly provided
     */
    fun getEffectiveAndroidGalleryConfig(): AndroidGalleryConfig {
        return androidGalleryConfig ?: AndroidGalleryConfig.forMimeTypeStrings(mimeTypes)
    }
 }
