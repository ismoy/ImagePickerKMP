package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.PREFIX_COMPRESSED
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SIXTY_FIVE_THOUSAND_FIVE_HUNDRED_THIRTY_SIX
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

 internal fun saveCompressedImage(
    bitmap: Bitmap,
    originalFile: File,
    compressionLevel: CompressionLevel
): File {
    val parentDir = originalFile.parentFile ?: originalFile.canonicalFile.parentFile
        ?: throw IllegalStateException("Cannot resolve parent directory for: ${originalFile.absolutePath}")
    val outputFile = File(parentDir, "$PREFIX_COMPRESSED${originalFile.name}")
    val quality = compressionLevel.toJpegQuality()

    BufferedOutputStream(FileOutputStream(outputFile), SIXTY_FIVE_THOUSAND_FIVE_HUNDRED_THIRTY_SIX).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }

    return outputFile
}