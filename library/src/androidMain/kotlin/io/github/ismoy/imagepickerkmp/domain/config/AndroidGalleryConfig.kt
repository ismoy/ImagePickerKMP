package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.MimeType

data class AndroidGalleryConfig(
    val forceGalleryOnly: Boolean = true,
    val localOnly: Boolean = false 
) {
    companion object {
        fun forMimeTypes(mimeTypes: List<MimeType>): AndroidGalleryConfig {
            val hasPdfTypes = mimeTypes.any { 
                it == MimeType.APPLICATION_PDF || it.value.contains("pdf", ignoreCase = true)
            }
            val hasNonImageTypes = mimeTypes.any { 
                !it.value.startsWith("image/", ignoreCase = true) && it != MimeType.IMAGE_ALL
            }
            
            val shouldUseGenericPicker = hasPdfTypes || hasNonImageTypes
            
            return AndroidGalleryConfig(
                forceGalleryOnly = !shouldUseGenericPicker,
                localOnly = false
            )
        }
        
        fun forMimeTypeStrings(mimeTypes: List<String>): AndroidGalleryConfig {
            val hasPdfTypes = mimeTypes.any { 
                it.contains("pdf", ignoreCase = true) || it.contains("application/pdf", ignoreCase = true)
            }
            val hasNonImageTypes = mimeTypes.any { 
                !it.startsWith("image/", ignoreCase = true) && it != "image/*"
            }
            
            val shouldUseGenericPicker = hasPdfTypes || hasNonImageTypes
            
            return AndroidGalleryConfig(
                forceGalleryOnly = !shouldUseGenericPicker,
                localOnly = false
            )
        }
    }
}
