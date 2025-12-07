package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException

 fun validateFile(fileData: ByteArray, mimeType: String?) {
    val maxFileSize = 20 * 1024 * 1024

    if (fileData.size > maxFileSize) {
        throw CloudOCRException("File size too large: ${fileData.size} bytes. Maximum allowed: $maxFileSize bytes (20MB)")
    }

    val supportedMimeTypes = listOf(
        "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif",
        "application/pdf"
    )

    val actualMimeType = mimeType ?: "image/jpeg"
    if (!supportedMimeTypes.contains(actualMimeType.lowercase())) {
        throw CloudOCRException("Unsupported file format: $actualMimeType. Supported formats: ${supportedMimeTypes.joinToString(", ")}")
    }

    if (actualMimeType.equals("application/pdf", ignoreCase = true)) {
        val pdfSignature = byteArrayOf(0x25, 0x50, 0x44, 0x46)
        if (fileData.size < 4 || !fileData.sliceArray(0..3).contentEquals(pdfSignature)) {
            throw CloudOCRException("File does not appear to be a valid PDF despite mimeType being application/pdf")
        }
    }
}
