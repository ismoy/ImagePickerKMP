package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)

class CompressionLevelTest {

    @Test
    fun testToQualityValue_lowCompression() {
        assertEquals(0.98, CompressionLevel.LOW.toQualityValue())
    }

    @Test
    fun testToQualityValue_mediumCompression() {
        assertEquals(0.85, CompressionLevel.MEDIUM.toQualityValue())
    }

    @Test
    fun testToQualityValue_highCompression() {
        assertEquals(0.65, CompressionLevel.HIGH.toQualityValue())
    }
}
