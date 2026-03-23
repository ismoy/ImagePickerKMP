package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.APPLICATION_PDF_TEXT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.IMAGE_PREFIX_TEXT_ALL
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SUFFIX_PDF

internal data class AndroidGalleryConfig(
    val forceGalleryOnly: Boolean = true,
    val localOnly: Boolean = false 
) {
    companion object {
        
        fun forMimeTypeStrings(mimeTypes: List<String>): AndroidGalleryConfig {
            val hasPdfTypes = mimeTypes.any { 
                it.contains(SUFFIX_PDF, ignoreCase = true) || it.contains(APPLICATION_PDF_TEXT, ignoreCase = true)
            }
            val hasNonImageTypes = mimeTypes.any { 
                !it.startsWith(IMAGE_PREFIX_TEXT, ignoreCase = true) && it != IMAGE_PREFIX_TEXT_ALL
            }
            
            val shouldUseGenericPicker = hasPdfTypes || hasNonImageTypes
            
            return AndroidGalleryConfig(
                forceGalleryOnly = !shouldUseGenericPicker,
                localOnly = false
            )
        }
    }
}
