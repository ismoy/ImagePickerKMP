package io.github.ismoy.imagepickerkmp.domain.utils

/**
 * Utility object for OCR-related calculations and file operations
 */
object OCRUtils {
    
    /**
     * Calculates appropriate max tokens based on file size and type
     */
    fun calculateMaxTokens(fileSizeBytes: Int, mimeType: String): Int {
        val baseSizeKB = fileSizeBytes / 1024.0
        
        return when {
            mimeType == "application/pdf" -> when {
                baseSizeKB < 500 -> 8000
                baseSizeKB < 2048 -> 16000
                baseSizeKB < 5120 -> 24000
                baseSizeKB < 10240 -> 32000
                else -> 40000
            }
            else -> when {
                baseSizeKB < 1024 -> 4000
                baseSizeKB < 5120 -> 8000
                baseSizeKB < 10240 -> 12000
                else -> 16000
            }
        }
    }

    /**
     * Calculates appropriate timeout based on file size and type
     */
    fun calculateTimeout(fileSizeBytes: Int, mimeType: String): Long {
        val baseSizeKB = fileSizeBytes / 1024.0
        
        return when {
            mimeType == "application/pdf" -> when {
                baseSizeKB < 500 -> 120_000L
                baseSizeKB < 2048 -> 300_000L
                baseSizeKB < 5120 -> 480_000L
                baseSizeKB < 10240 -> 600_000L
                else -> 720_000L
            }
            
            // Images are generally faster
            else -> when {
                baseSizeKB < 1024 -> 30_000L
                baseSizeKB < 5120 -> 90_000L
                baseSizeKB < 10240 -> 180_000L
                else -> 300_000L
            }
        }
    }

    /**
     * Detects the MIME type based on file signature and filename
     */
    fun detectMimeType(fileData: ByteArray, fileName: String?): String {
        return when {
            fileData.size >= 4 && fileData.sliceArray(0..3).contentEquals(byteArrayOf(0x25, 0x50, 0x44, 0x46)) ->
                "application/pdf"
            fileData.size >= 8 && fileData.sliceArray(0..7).contentEquals(byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) ->
                "image/png"
            fileData.size >= 3 && fileData[0] == 0xFF.toByte() && fileData[1] == 0xD8.toByte() && fileData[2] == 0xFF.toByte() ->
                "image/jpeg"
            fileData.size >= 12 && fileData.sliceArray(8..11).decodeToString() == "WEBP" ->
                "image/webp"
            fileData.size >= 6 && (fileData.sliceArray(0..5).decodeToString() == "GIF87a" || fileData.sliceArray(0..5).decodeToString() == "GIF89a") ->
                "image/gif"
            fileName != null -> when (fileName.substringAfterLast('.', "").lowercase()) {
                "pdf" -> "application/pdf"
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                else -> "image/jpeg"
            }
            else -> "image/jpeg"
        }
    }
}
