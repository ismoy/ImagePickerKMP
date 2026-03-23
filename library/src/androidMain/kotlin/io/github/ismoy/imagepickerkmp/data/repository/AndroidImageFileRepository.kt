package io.github.ismoy.imagepickerkmp.data.repository

import android.net.Uri
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.domain.repository.ImageFileRepository
import java.io.File

internal class AndroidImageFileRepository(
    private val fileManager: FileManager
) : ImageFileRepository {

    override fun createImageFile(): String {
        val file = fileManager.createImageFile()
        return file.absolutePath
    }

    override fun filePathToUriString(filePath: String): String =
        Uri.fromFile(File(filePath)).toString()
}
