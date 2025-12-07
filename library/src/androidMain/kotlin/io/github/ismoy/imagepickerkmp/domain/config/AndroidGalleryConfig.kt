package io.github.ismoy.imagepickerkmp.domain.config

import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * Configuration options for gallery picker behavior on Android.
 * Allows choosing between different picker strategies to handle different device behaviors.
 */
data class AndroidGalleryConfig(
    /**
     * Force the use of gallery-only picker instead of generic file picker.
     * 
     * When true: Uses Intent.ACTION_PICK with MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     *            This ensures the gallery app opens instead of file manager.
     * 
     * When false: Uses ActivityResultContracts.GetContent() (default behavior)
     *             This may open file manager on some devices.
     * 
     * Default: true (to ensure gallery opens), but automatically set to false if PDFs are requested
     */
    val forceGalleryOnly: Boolean = true,
    
    /**
     * Include local images only (not cloud storage).
     * This adds EXTRA_LOCAL_ONLY to the intent.
     * 
     * Default: true
     */
    val localOnly: Boolean = true
) {
    companion object {
        /**
         * Creates an AndroidGalleryConfig that automatically determines the best picker strategy
         * based on the requested MIME types.
         * 
         * @param mimeTypes List of MIME types to be selected
         * @return AndroidGalleryConfig with appropriate settings
         */
        fun forMimeTypes(mimeTypes: List<MimeType>): AndroidGalleryConfig {
            val hasPdfTypes = mimeTypes.any { 
                it == MimeType.APPLICATION_PDF || it.value.contains("pdf", ignoreCase = true)
            }
            val hasNonImageTypes = mimeTypes.any { 
                !it.value.startsWith("image/", ignoreCase = true) && it != MimeType.IMAGE_ALL
            }
            
            // Use generic picker for PDFs or non-image types
            val shouldUseGenericPicker = hasPdfTypes || hasNonImageTypes
            
            return AndroidGalleryConfig(
                forceGalleryOnly = !shouldUseGenericPicker,
                localOnly = true
            )
        }
        
        /**
         * Creates an AndroidGalleryConfig for string-based MIME types
         */
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
                localOnly = true
            )
        }
    }
}
