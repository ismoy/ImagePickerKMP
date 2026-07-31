package io.github.ismoy.imagepickerkmp.picker

import io.github.ismoy.imagepickerkmp.picker.PhotoResult

sealed class ImagePickerResult {
    data object Idle : ImagePickerResult()
    data object Loading : ImagePickerResult()
    data class Success(val photos: List<PhotoResult>) : ImagePickerResult() {
        val first: PhotoResult? get() = photos.firstOrNull()
    }
    data object Dismissed : ImagePickerResult()
    data class Error(val exception: Exception) : ImagePickerResult()
}
