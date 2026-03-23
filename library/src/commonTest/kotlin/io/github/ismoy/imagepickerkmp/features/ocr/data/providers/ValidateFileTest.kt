package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import io.github.ismoy.imagepickerkmp.features.ocr.utils.CloudOCRException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests para [validateFile] — validación de archivo antes de enviar a la red.
 *
 * Cubre:
 * - Archivo válido pasa sin excepciones
 * - Archivo mayor de 20 MB lanza [CloudOCRException]
 * - MIME type no soportado lanza [CloudOCRException]
 * - MIME null se trata como image/jpeg (válido)
 * - PDF sin cabecera %PDF lanza [CloudOCRException]
 * - PDF con cabecera correcta pasa validación
 */
class ValidateFileTest {

    private val validJpegBytes = ByteArray(100) { it.toByte() }
    private val validPdfBytes = byteArrayOf(0x25, 0x50, 0x44, 0x46) + ByteArray(100)

    // ── Tipos de imagen soportados ─────────────────────────────────────────────

    @Test
    fun jpeg_validSize_doesNotThrow() {
        validateFile(validJpegBytes, "image/jpeg")
    }

    @Test
    fun jpg_validSize_doesNotThrow() {
        validateFile(validJpegBytes, "image/jpg")
    }

    @Test
    fun png_validSize_doesNotThrow() {
        validateFile(validJpegBytes, "image/png")
    }

    @Test
    fun webp_validSize_doesNotThrow() {
        validateFile(validJpegBytes, "image/webp")
    }

    @Test
    fun gif_validSize_doesNotThrow() {
        validateFile(validJpegBytes, "image/gif")
    }

    @Test
    fun mimeTypeNull_treatedAsJpeg_doesNotThrow() {
        validateFile(validJpegBytes, null)
    }

    // ── PDF ────────────────────────────────────────────────────────────────────

    @Test
    fun pdf_withValidMagicBytes_doesNotThrow() {
        validateFile(validPdfBytes, "application/pdf")
    }

    @Test
    fun pdf_withoutMagicBytes_throwsCloudOCRException() {
        val fakePdf = ByteArray(100) { 0x00 }
        val ex = assertFailsWith<CloudOCRException> {
            validateFile(fakePdf, "application/pdf")
        }
        assertTrue(ex.message!!.contains("PDF", ignoreCase = true))
    }

    @Test
    fun pdf_tooShort_throwsCloudOCRException() {
        val tinyPdf = byteArrayOf(0x25, 0x50) // solo 2 bytes
        assertFailsWith<CloudOCRException> {
            validateFile(tinyPdf, "application/pdf")
        }
    }

    // ── Tamaño de archivo ─────────────────────────────────────────────────────

    @Test
    fun exactlyAtLimit_doesNotThrow() {
        val exactLimit = ByteArray(20 * 1024 * 1024) // exactamente 20 MB
        validateFile(exactLimit, "image/jpeg")
    }

    @Test
    fun oneByteOverLimit_throwsCloudOCRException() {
        val overLimit = ByteArray(20 * 1024 * 1024 + 1)
        val ex = assertFailsWith<CloudOCRException> {
            validateFile(overLimit, "image/jpeg")
        }
        assertTrue(
            ex.message!!.contains("too large", ignoreCase = true) ||
            ex.message!!.contains("size", ignoreCase = true),
            "Expected size error but got: ${ex.message}"
        )
    }

    @Test
    fun largeFile_21MB_throwsCloudOCRException() {
        val bigFile = ByteArray(21 * 1024 * 1024)
        assertFailsWith<CloudOCRException> {
            validateFile(bigFile, "image/png")
        }
    }

    // ── MIME types no soportados ──────────────────────────────────────────────

    @Test
    fun unsupportedMime_exe_throwsCloudOCRException() {
        val ex = assertFailsWith<CloudOCRException> {
            validateFile(validJpegBytes, "application/exe")
        }
        assertTrue(
            ex.message!!.contains("Unsupported", ignoreCase = true) ||
            ex.message!!.contains("format", ignoreCase = true),
            "Expected unsupported format message but got: ${ex.message}"
        )
    }

    @Test
    fun unsupportedMime_textHtml_throwsCloudOCRException() {
        assertFailsWith<CloudOCRException> {
            validateFile(validJpegBytes, "text/html")
        }
    }

    @Test
    fun unsupportedMime_videoMp4_throwsCloudOCRException() {
        assertFailsWith<CloudOCRException> {
            validateFile(validJpegBytes, "video/mp4")
        }
    }
}
