package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import java.io.File
import java.io.FileOutputStream

 internal fun saveCompressedImage(
    bitmap: Bitmap,
    originalFile: File,
    compressionLevel: CompressionLevel
): File {
    val parentDir = originalFile.parentFile ?: originalFile.canonicalFile.parentFile
        ?: throw IllegalStateException("Cannot resolve parent directory for: ${originalFile.absolutePath}")
    val outputFile = File(parentDir, "compressed_${originalFile.name}")
    val quality = (compressionLevel.toQualityValue() * 100).toInt()

    FileOutputStream(outputFile).use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }

    return outputFile
}