package io.github.ismoy.imagepickerkmp.domain.repository

internal interface ImageFileRepository {

    fun createImageFile(): String
    fun filePathToUriString(filePath: String): String
}
