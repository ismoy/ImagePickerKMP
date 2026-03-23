package io.github.ismoy.imagepickerkmp.features.ocr.domain.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests para [OCRUtils] — lógica pura de cálculo de tokens y timeouts.
 *
 * Cubre todas las ramas de [calculateMaxTokens] y [calculateTimeout]
 * tanto para imágenes como para PDFs.
 */
class OCRUtilsTest {

    // ── calculateMaxTokens — imágenes ──────────────────────────────────────────

    @Test
    fun tokens_imageUnder1024KB_returns4000() {
        val result = OCRUtils.calculateMaxTokens(500 * 1024, "image/jpeg")
        assertEquals(4000, result)
    }

    @Test
    fun tokens_imageBetween1024And5120KB_returns8000() {
        val result = OCRUtils.calculateMaxTokens(2048 * 1024, "image/png")
        assertEquals(8000, result)
    }

    @Test
    fun tokens_imageBetween5120And10240KB_returns12000() {
        val result = OCRUtils.calculateMaxTokens(7000 * 1024, "image/webp")
        assertEquals(12000, result)
    }

    @Test
    fun tokens_imageOver10240KB_returns16000() {
        val result = OCRUtils.calculateMaxTokens(15000 * 1024, "image/jpeg")
        assertEquals(16000, result)
    }

    // ── calculateMaxTokens — PDF ───────────────────────────────────────────────

    @Test
    fun tokens_pdfUnder500KB_returns8000() {
        val result = OCRUtils.calculateMaxTokens(100 * 1024, "application/pdf")
        assertEquals(8000, result)
    }

    @Test
    fun tokens_pdfBetween500And2048KB_returns16000() {
        val result = OCRUtils.calculateMaxTokens(1000 * 1024, "application/pdf")
        assertEquals(16000, result)
    }

    @Test
    fun tokens_pdfBetween2048And5120KB_returns24000() {
        val result = OCRUtils.calculateMaxTokens(3000 * 1024, "application/pdf")
        assertEquals(24000, result)
    }

    @Test
    fun tokens_pdfBetween5120And10240KB_returns32000() {
        val result = OCRUtils.calculateMaxTokens(8000 * 1024, "application/pdf")
        assertEquals(32000, result)
    }

    @Test
    fun tokens_pdfOver10240KB_returns40000() {
        val result = OCRUtils.calculateMaxTokens(15000 * 1024, "application/pdf")
        assertEquals(40000, result)
    }

    // ── calculateTimeout — imágenes ───────────────────────────────────────────

    @Test
    fun timeout_imageUnder1024KB_returns30000ms() {
        val result = OCRUtils.calculateTimeout(500 * 1024, "image/jpeg")
        assertEquals(30_000L, result)
    }

    @Test
    fun timeout_imageBetween1024And5120KB_returns90000ms() {
        val result = OCRUtils.calculateTimeout(2048 * 1024, "image/png")
        assertEquals(90_000L, result)
    }

    @Test
    fun timeout_imageBetween5120And10240KB_returns180000ms() {
        val result = OCRUtils.calculateTimeout(7000 * 1024, "image/webp")
        assertEquals(180_000L, result)
    }

    @Test
    fun timeout_imageOver10240KB_returns300000ms() {
        val result = OCRUtils.calculateTimeout(15000 * 1024, "image/jpeg")
        assertEquals(300_000L, result)
    }

    // ── calculateTimeout — PDF ─────────────────────────────────────────────────

    @Test
    fun timeout_pdfUnder500KB_returns120000ms() {
        val result = OCRUtils.calculateTimeout(100 * 1024, "application/pdf")
        assertEquals(120_000L, result)
    }

    @Test
    fun timeout_pdfBetween500And2048KB_returns300000ms() {
        val result = OCRUtils.calculateTimeout(1000 * 1024, "application/pdf")
        assertEquals(300_000L, result)
    }

    @Test
    fun timeout_pdfBetween2048And5120KB_returns480000ms() {
        val result = OCRUtils.calculateTimeout(3000 * 1024, "application/pdf")
        assertEquals(480_000L, result)
    }

    @Test
    fun timeout_pdfBetween5120And10240KB_returns600000ms() {
        val result = OCRUtils.calculateTimeout(8000 * 1024, "application/pdf")
        assertEquals(600_000L, result)
    }

    @Test
    fun timeout_pdfOver10240KB_returns720000ms() {
        val result = OCRUtils.calculateTimeout(15000 * 1024, "application/pdf")
        assertEquals(720_000L, result)
    }

    // ── tokens y timeout son siempre positivos ─────────────────────────────────

    @Test
    fun tokens_allCombinations_alwaysPositive() {
        val sizes = listOf(1, 100 * 1024, 1024 * 1024, 5 * 1024 * 1024, 15 * 1024 * 1024)
        val mimes = listOf("image/jpeg", "image/png", "application/pdf")
        sizes.forEach { size ->
            mimes.forEach { mime ->
                val tokens = OCRUtils.calculateMaxTokens(size, mime)
                assertTrue(tokens > 0, "tokens must be positive for size=$size mime=$mime")
            }
        }
    }

    @Test
    fun timeout_allCombinations_alwaysPositive() {
        val sizes = listOf(1, 100 * 1024, 1024 * 1024, 5 * 1024 * 1024, 15 * 1024 * 1024)
        val mimes = listOf("image/jpeg", "image/png", "application/pdf")
        sizes.forEach { size ->
            mimes.forEach { mime ->
                val timeout = OCRUtils.calculateTimeout(size, mime)
                assertTrue(timeout > 0L, "timeout must be positive for size=$size mime=$mime")
            }
        }
    }
}
