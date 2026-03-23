package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MimeTypeFullTest {

    // ───────────── Enum values ─────────────

    @Test
    fun mimeType_hasExpectedValues() {
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
        assertEquals("image/jpeg", MimeType.IMAGE_JPEG.value)
        assertEquals("image/png", MimeType.IMAGE_PNG.value)
        assertEquals("image/gif", MimeType.IMAGE_GIF.value)
        assertEquals("image/webp", MimeType.IMAGE_WEBP.value)
        assertEquals("image/bmp", MimeType.IMAGE_BMP.value)
        assertEquals("image/heic", MimeType.IMAGE_HEIC.value)
        assertEquals("image/heif", MimeType.IMAGE_HEIF.value)
        assertEquals("application/pdf", MimeType.APPLICATION_PDF.value)
    }

    @Test
    fun mimeType_enumContainsAllNineValues() {
        assertEquals(9, MimeType.entries.size)
    }

    // ───────────── toMimeTypeStrings ─────────────

    @Test
    fun toMimeTypeStrings_singleType_returnsCorrectList() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG)
        assertEquals(listOf("image/jpeg"), result)
    }

    @Test
    fun toMimeTypeStrings_multipleTypes_returnsCorrectList() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)
        assertEquals(listOf("image/jpeg", "image/png"), result)
    }

    @Test
    fun toMimeTypeStrings_emptyVararg_returnsEmptyList() {
        val result = MimeType.toMimeTypeStrings()
        assertTrue(result.isEmpty())
    }

    @Test
    fun toMimeTypeStrings_allTypes_returnsAllValues() {
        val all = MimeType.entries.toTypedArray()
        val result = MimeType.toMimeTypeStrings(*all)
        assertEquals(MimeType.entries.size, result.size)
    }

    // ───────────── COMMON_IMAGE_TYPES ─────────────

    @Test
    fun commonImageTypes_containsJpegPngGifWebp() {
        assertTrue(MimeType.IMAGE_JPEG in MimeType.COMMON_IMAGE_TYPES)
        assertTrue(MimeType.IMAGE_PNG in MimeType.COMMON_IMAGE_TYPES)
        assertTrue(MimeType.IMAGE_GIF in MimeType.COMMON_IMAGE_TYPES)
        assertTrue(MimeType.IMAGE_WEBP in MimeType.COMMON_IMAGE_TYPES)
    }

    @Test
    fun commonImageTypes_doesNotContainPdf() {
        assertTrue(MimeType.APPLICATION_PDF !in MimeType.COMMON_IMAGE_TYPES)
    }

    @Test
    fun commonImageTypes_hasExactlyFourEntries() {
        assertEquals(4, MimeType.COMMON_IMAGE_TYPES.size)
    }

    // ───────────── ALL_SUPPORTED_TYPES ─────────────

    @Test
    fun allSupportedTypes_containsAllEnumValues() {
        MimeType.entries.forEach { type ->
            assertTrue(type in MimeType.ALL_SUPPORTED_TYPES, "$type should be in ALL_SUPPORTED_TYPES")
        }
    }

    @Test
    fun allSupportedTypes_sizeEqualsEntriesCount() {
        assertEquals(MimeType.entries.size, MimeType.ALL_SUPPORTED_TYPES.size)
    }

    // ───────────── fromString ─────────────

    @Test
    fun fromString_exactMatch_returnsCorrectType() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("image/jpeg"))
        assertEquals(MimeType.IMAGE_PNG, MimeType.fromString("image/png"))
        assertEquals(MimeType.APPLICATION_PDF, MimeType.fromString("application/pdf"))
    }

    @Test
    fun fromString_caseInsensitive_returnsCorrectType() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("IMAGE/JPEG"))
        assertEquals(MimeType.IMAGE_PNG, MimeType.fromString("Image/PNG"))
        assertEquals(MimeType.IMAGE_WEBP, MimeType.fromString("IMAGE/WEBP"))
    }

    @Test
    fun fromString_unknownType_returnsNull() {
        assertNull(MimeType.fromString("text/plain"))
        assertNull(MimeType.fromString("video/mp4"))
        assertNull(MimeType.fromString(""))
    }

    @Test
    fun fromString_allEnumValues_roundtrip() {
        MimeType.entries.forEach { type ->
            val found = MimeType.fromString(type.value)
            assertNotNull(found, "fromString(${type.value}) should not return null")
            assertEquals(type, found)
        }
    }

    // ───────────── value property ─────────────

    @Test
    fun allMimeTypes_valueIsNotEmpty() {
        MimeType.entries.forEach { type ->
            assertTrue(type.value.isNotEmpty(), "$type value should not be empty")
        }
    }

    @Test
    fun allMimeTypes_valueContainsSlash() {
        MimeType.entries.forEach { type ->
            assertTrue(type.value.contains('/'), "$type value should contain '/'")
        }
    }
}
