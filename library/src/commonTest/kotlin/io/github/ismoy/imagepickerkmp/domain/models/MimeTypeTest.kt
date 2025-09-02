package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MimeTypeTest {

    @Test
    fun `should have correct MIME type values`() {
        assertEquals("image/*", MimeType.IMAGE_ALL.value)
        assertEquals("image/jpeg", MimeType.IMAGE_JPEG.value)
        assertEquals("image/png", MimeType.IMAGE_PNG.value)
        assertEquals("image/gif", MimeType.IMAGE_GIF.value)
        assertEquals("image/webp", MimeType.IMAGE_WEBP.value)
        assertEquals("image/bmp", MimeType.IMAGE_BMP.value)
        assertEquals("image/heic", MimeType.IMAGE_HEIC.value)
        assertEquals("image/heif", MimeType.IMAGE_HEIF.value)
    }

    @Test
    fun `toMimeTypeStrings should convert enum list to string list`() {
        val result = MimeType.toMimeTypeStrings(
            MimeType.IMAGE_JPEG,
            MimeType.IMAGE_PNG,
            MimeType.IMAGE_GIF
        )
        
        assertEquals(3, result.size)
        assertTrue(result.contains("image/jpeg"))
        assertTrue(result.contains("image/png"))
        assertTrue(result.contains("image/gif"))
    }

    @Test
    fun `toMimeTypeStrings should handle empty input`() {
        val result = MimeType.toMimeTypeStrings()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `toMimeTypeStrings should handle single input`() {
        val result = MimeType.toMimeTypeStrings(MimeType.IMAGE_ALL)
        
        assertEquals(1, result.size)
        assertEquals("image/*", result.first())
    }

    @Test
    fun `COMMON_IMAGE_TYPES should contain expected types`() {
        val commonTypes = MimeType.COMMON_IMAGE_TYPES
        
        assertEquals(4, commonTypes.size)
        assertTrue(commonTypes.contains(MimeType.IMAGE_JPEG))
        assertTrue(commonTypes.contains(MimeType.IMAGE_PNG))
        assertTrue(commonTypes.contains(MimeType.IMAGE_GIF))
        assertTrue(commonTypes.contains(MimeType.IMAGE_WEBP))
    }

    @Test
    fun `ALL_SUPPORTED_TYPES should contain all enum values`() {
        val allTypes = MimeType.ALL_SUPPORTED_TYPES
        val enumValues = MimeType.values()
        
        assertEquals(enumValues.size, allTypes.size)
        enumValues.forEach { enumValue ->
            assertTrue(allTypes.contains(enumValue))
        }
    }

    @Test
    fun `fromString should find existing MIME type`() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("image/jpeg"))
        assertEquals(MimeType.IMAGE_PNG, MimeType.fromString("image/png"))
        assertEquals(MimeType.IMAGE_ALL, MimeType.fromString("image/*"))
    }

    @Test
    fun `fromString should be case insensitive`() {
        assertEquals(MimeType.IMAGE_JPEG, MimeType.fromString("IMAGE/JPEG"))
        assertEquals(MimeType.IMAGE_PNG, MimeType.fromString("Image/PNG"))
        assertEquals(MimeType.IMAGE_GIF, MimeType.fromString("image/GIF"))
    }

    @Test
    fun `fromString should return null for unknown MIME type`() {
        assertNull(MimeType.fromString("video/mp4"))
        assertNull(MimeType.fromString("application/pdf"))
        assertNull(MimeType.fromString("unknown/type"))
        assertNull(MimeType.fromString(""))
    }

    @Test
    fun `enum should have proper toString behavior`() {
        assertEquals("IMAGE_ALL", MimeType.IMAGE_ALL.toString())
        assertEquals("IMAGE_JPEG", MimeType.IMAGE_JPEG.toString())
        assertEquals("IMAGE_PNG", MimeType.IMAGE_PNG.toString())
    }

    @Test
    fun `value property should return correct string`() {
        MimeType.values().forEach { mimeType ->
            assertNotNull(mimeType.value)
            assertTrue(mimeType.value.startsWith("image/"))
        }
    }

    @Test
    fun `should handle all values correctly in companion functions`() {
        val allValues = MimeType.values()
        val stringValues = MimeType.toMimeTypeStrings(*allValues)
        
        assertEquals(allValues.size, stringValues.size)
        
        allValues.forEachIndexed { index, mimeType ->
            assertEquals(mimeType.value, stringValues[index])
        }
    }
}
