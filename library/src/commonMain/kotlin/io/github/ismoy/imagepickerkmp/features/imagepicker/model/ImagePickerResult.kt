package io.github.ismoy.imagepickerkmp.features.imagepicker.model

import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
sealed class ImagePickerResult {
    data object Idle : ImagePickerResult()
    data object Loading : ImagePickerResult()
    data class Success(val photos: List<PhotoResult>) : ImagePickerResult() {
        val first: PhotoResult? get() = photos.firstOrNull()
    }
    data object Dismissed : ImagePickerResult()
    data class Error(val exception: Exception) : ImagePickerResult()
}
