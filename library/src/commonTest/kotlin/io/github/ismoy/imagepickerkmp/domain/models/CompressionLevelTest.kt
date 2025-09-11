package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals

class CompressionLevelTest {

    @Test
    fun testToQualityValue_lowCompression() {
        assertEquals(0.95, CompressionLevel.LOW.toQualityValue())
    }

    @Test
    fun testToQualityValue_mediumCompression() {
        assertEquals(0.75, CompressionLevel.MEDIUM.toQualityValue())
    }

    @Test
    fun testToQualityValue_highCompression() {
        assertEquals(0.50, CompressionLevel.HIGH.toQualityValue())
    }

    @Test
    fun testAllCompressionLevels_haveValidQualityValues() {
        for (level in CompressionLevel.entries) {
            val quality = level.toQualityValue()
            assert(quality >= 0.0 && quality <= 1.0) {
                "Quality value for $level should be between 0.0 and 1.0, but was $quality"
            }
        }
    }

    @Test
    fun testCompressionLevels_qualityDescending() {
        val lowQuality = CompressionLevel.LOW.toQualityValue()
        val mediumQuality = CompressionLevel.MEDIUM.toQualityValue()
        val highQuality = CompressionLevel.HIGH.toQualityValue()
        
        assert(lowQuality > mediumQuality) {
            "LOW compression should have higher quality than MEDIUM"
        }
        assert(mediumQuality > highQuality) {
            "MEDIUM compression should have higher quality than HIGH"
        }
    }
}
