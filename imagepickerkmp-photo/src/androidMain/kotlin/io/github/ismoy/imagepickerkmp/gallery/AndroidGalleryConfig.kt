package io.github.ismoy.imagepickerkmp.gallery

import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT
import io.github.ismoy.imagepickerkmp.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT_ALL

internal data class AndroidGalleryConfig(
    val forceGalleryOnly: Boolean = true,
    val localOnly: Boolean = false 
) {
    companion object {
        
        fun forMimeTypeStrings(mimeTypes: List<String>): AndroidGalleryConfig {
            val hasNonImageTypes = mimeTypes.any { 
                !it.startsWith(IMAGE_PREFIX_TEXT, ignoreCase = true) && it != IMAGE_PREFIX_TEXT_ALL
            }
            
            val shouldUseGenericPicker = hasNonImageTypes
            
            return AndroidGalleryConfig(
                forceGalleryOnly = !shouldUseGenericPicker,
                localOnly = false
            )
        }
    }
}
