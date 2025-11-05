package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun createFileChooser(
    mimeTypes: List<MimeType>, 
    title: String, 
    multiSelection: Boolean,
    filterDescription: String = "Image files"
): JFileChooser {
    return JFileChooser().apply {
        dialogTitle = title
        fileSelectionMode = JFileChooser.FILES_ONLY
        isMultiSelectionEnabled = multiSelection

        val extensions = mimeTypes.flatMap { mimeType ->
            when (mimeType) {
                MimeType.IMAGE_JPEG -> listOf("jpg", "jpeg")
                MimeType.IMAGE_PNG -> listOf("png")
                MimeType.IMAGE_GIF -> listOf("gif")
                MimeType.IMAGE_WEBP -> listOf("webp")
                MimeType.IMAGE_BMP -> listOf("bmp")
                MimeType.IMAGE_HEIC -> listOf("heic")
                MimeType.IMAGE_HEIF -> listOf("heif")
                MimeType.APPLICATION_PDF -> listOf("pdf")
                MimeType.IMAGE_ALL -> listOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif","pdf")
            }
        }

        if (extensions.isNotEmpty()) {
            fileFilter = FileNameExtensionFilter(filterDescription, *extensions.toTypedArray())
        }
    }
}
