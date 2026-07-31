package io.github.ismoy.imagepickerkmp.domain.models

import io.github.ismoy.imagepickerkmp.picker.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MimeTypeTest {

    // ── value property ────────────────────────────────────────────────────────

    @Test
    fun imageAll_value_isImageWildcard() {
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
    }

    @Test
    fun imageJpeg_value_isImageJpeg() {
        assertEquals("image/jpeg", MimeType.IMAGE_JPEG.value)
    }

    @Test
    fun imagePng_value_isImagePng() {
        assertEquals("image/png", MimeType.IMAGE_PNG.value)
    }

    @Test
    fun imageGif_value_isImageGif() {
        assertEquals("image/gif", MimeType.IMAGE_GIF.value)
    }

    @Test
    fun imageWebp_value_isImageWebp() {
        assertEquals("image/webp", MimeType.IMAGE_WEBP.value)
    }

    @Test
    fun imageBmp_value_isImageBmp() {
        assertEquals("image/bmp", MimeType.IMAGE_BMP.value)
    }

    @Test
    fun imageHeic_value_isImageHeic() {
        assertEquals("image/heic", MimeType.IMAGE_HEIC.value)
    }

    @Test
    fun imageHeif_value_isImageHeif() {
        assertEquals("image/heif", MimeType.IMAGE_HEIF.value)
    }

    @Test
    fun applicationPdf_value_isApplicationPdf() {
        assertEquals("application/pdf", MimeType.APPLICATION_PDF.value)
    }

    // ── toMimeTypeStrings ─────────────────────────────────────────────────────

    @Test
    fun toMimeTypeStrings_singleType_returnsSingleElement() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG)
        assertEquals(listOf("image/jpeg"), result)
    }

    @Test
    fun toMimeTypeStrings_multipleTypes_returnsAllValues() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG, MimeType.IMAGE_GIF)
        assertEquals(listOf("image/jpeg", "image/png", "image/gif"), result)
    }

    @Test
    fun toMimeTypeStrings_noArgs_returnsEmptyList() {
        val result = MimeType.toMimeTypeStrings()
        assertTrue(result.isEmpty())
    }

    @Test
    fun toMimeTypeStrings_withPdf_includesPdfString() {
        val result = MimeType.toMimeTypeStrings(MimeType.APPLICATION_PDF)
        assertEquals(listOf("application/pdf"), result)
    }

    @Test
    fun toMimeTypeStrings_allTypes_returnsNineElements() {
        val all = MimeType.entries.toTypedArray()
        val result = MimeType.toMimeTypeStrings(*all)
        assertEquals(9, result.size)
    }

    // ── COMMON_IMAGE_TYPES ────────────────────────────────────────────────────

    @Test
    fun commonImageTypes_containsFourTypes() {
        assertEquals(4, MimeType.COMMON_IMAGE_TYPES.size)
    }

    @Test
    fun commonImageTypes_containsJpeg() {
        assertTrue(MimeType.COMMON_IMAGE_TYPES.contains(MimeType.IMAGE_JPEG))
    }

    @Test
    fun commonImageTypes_containsPng() {
        assertTrue(MimeType.COMMON_IMAGE_TYPES.contains(MimeType.IMAGE_PNG))
    }

    @Test
    fun commonImageTypes_containsGif() {
        assertTrue(MimeType.COMMON_IMAGE_TYPES.contains(MimeType.IMAGE_GIF))
    }

    @Test
    fun commonImageTypes_containsWebp() {
        assertTrue(MimeType.COMMON_IMAGE_TYPES.contains(MimeType.IMAGE_WEBP))
    }

    @Test
    fun commonImageTypes_doesNotContainPdf() {
        assertTrue(!MimeType.COMMON_IMAGE_TYPES.contains(MimeType.APPLICATION_PDF))
    }

    // ── ALL_SUPPORTED_TYPES ───────────────────────────────────────────────────

    @Test
    fun allSupportedTypes_containsNineTypes() {
        assertEquals(9, MimeType.ALL_SUPPORTED_TYPES.size)
    }

    @Test
    fun allSupportedTypes_containsAllEnumEntries() {
        val supported = MimeType.ALL_SUPPORTED_TYPES
        MimeType.entries.forEach { type ->
            assertTrue(supported.contains(type), "Missing: $type")
        }
    }

    // ── fromString ────────────────────────────────────────────────────────────

    @Test
    fun fromString_exactMatch_returnsCorrectType() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("image/jpeg"))
    }

    @Test
    fun fromString_caseInsensitive_upperCase_returnsType() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("IMAGE/JPEG"))
    }

    @Test
    fun fromString_caseInsensitive_mixedCase_returnsType() {
        assertEquals(MimeType.IMAGE_PNG, MimeType.fromString("Image/PNG"))
    }

    @Test
    fun fromString_unknownString_returnsNull() {
        assertNull(MimeType.fromString("video/mp4"))
    }

    @Test
    fun fromString_emptyString_returnsNull() {
        assertNull(MimeType.fromString(""))
    }

    @Test
    fun fromString_pdfString_returnsPdf() {
        assertEquals(MimeType.APPLICATION_PDF, MimeType.fromString("application/pdf"))
    }

    @Test
    fun fromString_imageAll_returnsImageAll() {
        assertEquals(MimeType.IMAGE_ALL, MimeType.fromString("image/*"))
    }

    @Test
    fun fromString_heic_returnsHeic() {
        assertEquals(MimeType.IMAGE_HEIC, MimeType.fromString("image/heic"))
    }

    @Test
    fun fromString_heif_returnsHeif() {
        assertEquals(MimeType.IMAGE_HEIF, MimeType.fromString("image/heif"))
    }

    @Test
    fun fromString_bmp_returnsBmp() {
        assertEquals(MimeType.IMAGE_BMP, MimeType.fromString("image/bmp"))
    }

    @Test
    fun fromString_webp_returnsWebp() {
        assertEquals(MimeType.IMAGE_WEBP, MimeType.fromString("image/webp"))
    }

    @Test
    fun fromString_gif_returnsGif() {
        assertEquals(MimeType.IMAGE_GIF, MimeType.fromString("image/gif"))
    }

    // ── entries / name ────────────────────────────────────────────────────────

    @Test
    fun entries_containsNineElements() {
        assertEquals(9, MimeType.entries.size)
    }

    @Test
    fun name_imageJpeg_isIMAGE_JPEG() {
        assertEquals("IMAGE_JPEG", MimeType.IMAGE_JPEG.name)
    }

    @Test
    fun valueOf_IMAGE_PNG_returnsCorrectEntry() {
        assertEquals(MimeType.IMAGE_PNG, MimeType.valueOf("IMAGE_PNG"))
    }

    // ── ordinal ordering ──────────────────────────────────────────────────────

    @Test
    fun imageAll_isFirstEntry() {
        assertEquals(0, MimeType.IMAGE_ALL.ordinal)
    }

    @Test
    fun applicationPdf_isLastEntry() {
        assertEquals(MimeType.entries.size - 1, MimeType.APPLICATION_PDF.ordinal)
    }
}
