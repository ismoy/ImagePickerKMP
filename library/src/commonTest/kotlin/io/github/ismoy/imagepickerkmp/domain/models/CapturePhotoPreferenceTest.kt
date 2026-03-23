package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CapturePhotoPreferenceTest {

    // ───────────── Enum values ─────────────

    @Test
    fun capturePhotoPreference_hasExactlyThreeValues() {
        assertEquals(3, CapturePhotoPreference.entries.size)
    }

    @Test
    fun capturePhotoPreference_containsFastBalancedQuality() {
        val names = CapturePhotoPreference.entries.map { it.name }
        assertTrue("FAST" in names, "FAST should exist")
        assertTrue("BALANCED" in names, "BALANCED should exist")
        assertTrue("QUALITY" in names, "QUALITY should exist")
    }

    // ───────────── valueOf ─────────────

    @Test
    fun valueOf_fast_returnsCorrectEnum() {
        assertEquals(CapturePhotoPreference.FAST, CapturePhotoPreference.valueOf("FAST"))
    }

    @Test
    fun valueOf_balanced_returnsCorrectEnum() {
        assertEquals(CapturePhotoPreference.BALANCED, CapturePhotoPreference.valueOf("BALANCED"))
    }

    @Test
    fun valueOf_quality_returnsCorrectEnum() {
        assertEquals(CapturePhotoPreference.QUALITY, CapturePhotoPreference.valueOf("QUALITY"))
    }

    // ───────────── Equality ─────────────

    @Test
    fun sameEnumValue_isEqual() {
        val a = CapturePhotoPreference.FAST
        val b = CapturePhotoPreference.FAST
        assertEquals(a, b)
    }

    @Test
    fun differentEnumValues_areNotEqual() {
        assertTrue(CapturePhotoPreference.FAST != CapturePhotoPreference.QUALITY)
        assertTrue(CapturePhotoPreference.BALANCED != CapturePhotoPreference.FAST)
    }

    // ───────────── ordinal ordering ─────────────

    @Test
    fun fast_hasLowestOrdinal() {
        assertTrue(
            CapturePhotoPreference.FAST.ordinal < CapturePhotoPreference.BALANCED.ordinal,
            "FAST should come before BALANCED"
        )
        assertTrue(
            CapturePhotoPreference.BALANCED.ordinal < CapturePhotoPreference.QUALITY.ordinal,
            "BALANCED should come before QUALITY"
        )
    }

    // ───────────── name ─────────────

    @Test
    fun enumNames_areUpperCase() {
        CapturePhotoPreference.entries.forEach { pref ->
            assertEquals(pref.name, pref.name.uppercase())
        }
    }

    // ───────────── entries iterable ─────────────

    @Test
    fun entries_canBeIteratedWithoutError() {
        var count = 0
        CapturePhotoPreference.entries.forEach { _ -> count++ }
        assertEquals(3, count)
    }
}
