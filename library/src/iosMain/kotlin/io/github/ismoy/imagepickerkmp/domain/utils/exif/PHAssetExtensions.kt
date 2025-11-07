package io.github.ismoy.imagepickerkmp.domain.utils.exif

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Photos.PHAsset

/**
 * Extension functions for PHAsset to simplify EXIF data extraction.
 */
@OptIn(ExperimentalForeignApi::class)
internal object PHAssetExtensions {
    
    /**
     * Extracts latitude from PHAsset location.
     */
    fun PHAsset.extractLatitude(): Double? = 
        location?.coordinate?.useContents { latitude }
    
    /**
     * Extracts longitude from PHAsset location.
     */
    fun PHAsset.extractLongitude(): Double? = 
        location?.coordinate?.useContents { longitude }
}
