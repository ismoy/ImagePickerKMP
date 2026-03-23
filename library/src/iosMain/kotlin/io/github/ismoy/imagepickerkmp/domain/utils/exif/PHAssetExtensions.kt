package io.github.ismoy.imagepickerkmp.domain.utils.exif

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Photos.PHAsset

@OptIn(ExperimentalForeignApi::class)
internal object PHAssetExtensions {
    
    fun PHAsset.extractLatitude(): Double? = 
        location?.coordinate?.useContents { latitude }
    
    fun PHAsset.extractLongitude(): Double? = 
        location?.coordinate?.useContents { longitude }
}
