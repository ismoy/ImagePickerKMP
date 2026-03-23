package io.github.ismoy.imagepickerkmp.presentation.resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringResourceTest {

    // ───────────── Completeness ─────────────

    @Test
    fun stringResource_hasExpectedCount() {
        assertEquals(27, StringResource.entries.size)
    }

    @Test
    fun stringResource_containsAllExpectedKeys() {
        val expected = setOf(
            "CAMERA_PERMISSION_REQUIRED",
            "CAMERA_PERMISSION_DESCRIPTION",
            "OPEN_SETTINGS",
            "CAMERA_PERMISSION_DENIED",
            "CAMERA_PERMISSION_DENIED_DESCRIPTION",
            "GRANT_PERMISSION",
            "CAMERA_PERMISSION_PERMANENTLY_DENIED",
            "IMAGE_CONFIRMATION_TITLE",
            "ACCEPT_BUTTON",
            "RETRY_BUTTON",
            "SELECT_OPTION_DIALOG_TITLE",
            "TAKE_PHOTO_OPTION",
            "SELECT_FROM_GALLERY_OPTION",
            "CANCEL_OPTION",
            "PREVIEW_IMAGE_DESCRIPTION",
            "HD_QUALITY_DESCRIPTION",
            "SD_QUALITY_DESCRIPTION",
            "INVALID_CONTEXT_ERROR",
            "PHOTO_CAPTURE_ERROR",
            "GALLERY_SELECTION_ERROR",
            "PERMISSION_ERROR",
            "GALLERY_PERMISSION_REQUIRED",
            "GALLERY_PERMISSION_DESCRIPTION",
            "GALLERY_PERMISSION_DENIED",
            "GALLERY_PERMISSION_DENIED_DESCRIPTION",
            "GALLERY_GRANT_PERMISSION",
            "GALLERY_BTN_SETTINGS"
        )
        val actual = StringResource.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    // ───────────── Camera permission keys ─────────────

    @Test
    fun cameraPermissionKeys_exist() {
        val names = StringResource.entries.map { it.name }
        assertTrue("CAMERA_PERMISSION_REQUIRED" in names)
        assertTrue("CAMERA_PERMISSION_DESCRIPTION" in names)
        assertTrue("CAMERA_PERMISSION_DENIED" in names)
        assertTrue("CAMERA_PERMISSION_DENIED_DESCRIPTION" in names)
        assertTrue("CAMERA_PERMISSION_PERMANENTLY_DENIED" in names)
    }

    // ───────────── Gallery permission keys ─────────────

    @Test
    fun galleryPermissionKeys_exist() {
        val names = StringResource.entries.map { it.name }
        assertTrue("GALLERY_PERMISSION_REQUIRED" in names)
        assertTrue("GALLERY_PERMISSION_DESCRIPTION" in names)
        assertTrue("GALLERY_PERMISSION_DENIED" in names)
        assertTrue("GALLERY_PERMISSION_DENIED_DESCRIPTION" in names)
        assertTrue("GALLERY_GRANT_PERMISSION" in names)
        assertTrue("GALLERY_BTN_SETTINGS" in names)
    }

    // ───────────── UI action keys ─────────────

    @Test
    fun uiActionKeys_exist() {
        val names = StringResource.entries.map { it.name }
        assertTrue("ACCEPT_BUTTON" in names)
        assertTrue("RETRY_BUTTON" in names)
        assertTrue("CANCEL_OPTION" in names)
        assertTrue("OPEN_SETTINGS" in names)
        assertTrue("GRANT_PERMISSION" in names)
    }

    // ───────────── Error keys ─────────────

    @Test
    fun errorKeys_exist() {
        val names = StringResource.entries.map { it.name }
        assertTrue("PHOTO_CAPTURE_ERROR" in names)
        assertTrue("GALLERY_SELECTION_ERROR" in names)
        assertTrue("PERMISSION_ERROR" in names)
        assertTrue("INVALID_CONTEXT_ERROR" in names)
    }

    // ───────────── valueOf ─────────────

    @Test
    fun valueOf_knownKey_returnsCorrectEnum() {
        assertEquals(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.valueOf("CAMERA_PERMISSION_REQUIRED")
        )
        assertEquals(
            StringResource.ACCEPT_BUTTON,
            StringResource.valueOf("ACCEPT_BUTTON")
        )
    }

    // ───────────── No duplicates ─────────────

    @Test
    fun noTwoEntries_haveTheSameName() {
        val names = StringResource.entries.map { it.name }
        assertEquals(names.size, names.toSet().size, "All StringResource names should be unique")
    }

    // ───────────── ordinals are unique ─────────────

    @Test
    fun allOrdinals_areUnique() {
        val ordinals = StringResource.entries.map { it.ordinal }
        assertEquals(ordinals.size, ordinals.toSet().size, "All ordinals should be unique")
    }

    // ───────────── Iteration ─────────────

    @Test
    fun entries_canBeIteratedWithoutError() {
        var count = 0
        StringResource.entries.forEach { _ -> count++ }
        assertEquals(27, count)
    }
}
