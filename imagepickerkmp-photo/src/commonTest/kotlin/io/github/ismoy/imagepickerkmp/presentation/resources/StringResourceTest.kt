package io.github.ismoy.imagepickerkmp.presentation.resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringResourceTest {

    // ── Enum entries existence ────────────────────────────────────────────────

    @Test
    fun allEntries_count_is27() {
        assertEquals(27, StringResource.entries.size)
    }

    @Test
    fun cameraPermissionRequired_exists() {
        assertNotNull(StringResource.CAMERA_PERMISSION_REQUIRED)
    }

    @Test
    fun cameraPermissionDescription_exists() {
        assertNotNull(StringResource.CAMERA_PERMISSION_DESCRIPTION)
    }

    @Test
    fun openSettings_exists() {
        assertNotNull(StringResource.OPEN_SETTINGS)
    }

    @Test
    fun cameraPermissionDenied_exists() {
        assertNotNull(StringResource.CAMERA_PERMISSION_DENIED)
    }

    @Test
    fun cameraPermissionDeniedDescription_exists() {
        assertNotNull(StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION)
    }

    @Test
    fun grantPermission_exists() {
        assertNotNull(StringResource.GRANT_PERMISSION)
    }

    @Test
    fun cameraPermissionPermanentlyDenied_exists() {
        assertNotNull(StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED)
    }

    @Test
    fun imageConfirmationTitle_exists() {
        assertNotNull(StringResource.IMAGE_CONFIRMATION_TITLE)
    }

    @Test
    fun acceptButton_exists() {
        assertNotNull(StringResource.ACCEPT_BUTTON)
    }

    @Test
    fun retryButton_exists() {
        assertNotNull(StringResource.RETRY_BUTTON)
    }

    @Test
    fun selectOptionDialogTitle_exists() {
        assertNotNull(StringResource.SELECT_OPTION_DIALOG_TITLE)
    }

    @Test
    fun takePhotoOption_exists() {
        assertNotNull(StringResource.TAKE_PHOTO_OPTION)
    }

    @Test
    fun selectFromGalleryOption_exists() {
        assertNotNull(StringResource.SELECT_FROM_GALLERY_OPTION)
    }

    @Test
    fun cancelOption_exists() {
        assertNotNull(StringResource.CANCEL_OPTION)
    }

    @Test
    fun previewImageDescription_exists() {
        assertNotNull(StringResource.PREVIEW_IMAGE_DESCRIPTION)
    }

    @Test
    fun hdQualityDescription_exists() {
        assertNotNull(StringResource.HD_QUALITY_DESCRIPTION)
    }

    @Test
    fun sdQualityDescription_exists() {
        assertNotNull(StringResource.SD_QUALITY_DESCRIPTION)
    }

    @Test
    fun invalidContextError_exists() {
        assertNotNull(StringResource.INVALID_CONTEXT_ERROR)
    }

    @Test
    fun photoCaptureError_exists() {
        assertNotNull(StringResource.PHOTO_CAPTURE_ERROR)
    }

    @Test
    fun gallerySelectionError_exists() {
        assertNotNull(StringResource.GALLERY_SELECTION_ERROR)
    }

    @Test
    fun permissionError_exists() {
        assertNotNull(StringResource.PERMISSION_ERROR)
    }

    @Test
    fun galleryPermissionRequired_exists() {
        assertNotNull(StringResource.GALLERY_PERMISSION_REQUIRED)
    }

    @Test
    fun galleryPermissionDescription_exists() {
        assertNotNull(StringResource.GALLERY_PERMISSION_DESCRIPTION)
    }

    @Test
    fun galleryPermissionDenied_exists() {
        assertNotNull(StringResource.GALLERY_PERMISSION_DENIED)
    }

    @Test
    fun galleryPermissionDeniedDescription_exists() {
        assertNotNull(StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION)
    }

    @Test
    fun galleryGrantPermission_exists() {
        assertNotNull(StringResource.GALLERY_GRANT_PERMISSION)
    }

    @Test
    fun galleryBtnSettings_exists() {
        assertNotNull(StringResource.GALLERY_BTN_SETTINGS)
    }

    // ── Name consistency ──────────────────────────────────────────────────────

    @Test
    fun cameraPermissionRequired_name_isCorrect() {
        assertEquals("CAMERA_PERMISSION_REQUIRED", StringResource.CAMERA_PERMISSION_REQUIRED.name)
    }

    @Test
    fun openSettings_name_isCorrect() {
        assertEquals("OPEN_SETTINGS", StringResource.OPEN_SETTINGS.name)
    }

    @Test
    fun cancelOption_name_isCorrect() {
        assertEquals("CANCEL_OPTION", StringResource.CANCEL_OPTION.name)
    }

    // ── valueOf ───────────────────────────────────────────────────────────────

    @Test
    fun valueOf_CAMERA_PERMISSION_REQUIRED_returnsEntry() {
        assertEquals(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.valueOf("CAMERA_PERMISSION_REQUIRED")
        )
    }

    @Test
    fun valueOf_GALLERY_BTN_SETTINGS_returnsEntry() {
        assertEquals(
            StringResource.GALLERY_BTN_SETTINGS,
            StringResource.valueOf("GALLERY_BTN_SETTINGS")
        )
    }

    // ── Ordinal ordering ──────────────────────────────────────────────────────

    @Test
    fun cameraPermissionRequired_isFirstEntry() {
        assertEquals(0, StringResource.CAMERA_PERMISSION_REQUIRED.ordinal)
    }

    @Test
    fun allEntries_haveUniqueOrdinals() {
        val ordinals = StringResource.entries.map { it.ordinal }
        assertEquals(ordinals.size, ordinals.toSet().size)
    }

    @Test
    fun allEntries_haveUniqueNames() {
        val names = StringResource.entries.map { it.name }
        assertEquals(names.size, names.toSet().size)
    }

    // ── entries contains all known keys ───────────────────────────────────────

    @Test
    fun entries_containsAllKnownKeys() {
        val expected = setOf(
            "CAMERA_PERMISSION_REQUIRED", "CAMERA_PERMISSION_DESCRIPTION",
            "OPEN_SETTINGS", "CAMERA_PERMISSION_DENIED",
            "CAMERA_PERMISSION_DENIED_DESCRIPTION", "GRANT_PERMISSION",
            "CAMERA_PERMISSION_PERMANENTLY_DENIED", "IMAGE_CONFIRMATION_TITLE",
            "ACCEPT_BUTTON", "RETRY_BUTTON", "SELECT_OPTION_DIALOG_TITLE",
            "TAKE_PHOTO_OPTION", "SELECT_FROM_GALLERY_OPTION", "CANCEL_OPTION",
            "PREVIEW_IMAGE_DESCRIPTION", "HD_QUALITY_DESCRIPTION",
            "SD_QUALITY_DESCRIPTION", "INVALID_CONTEXT_ERROR",
            "PHOTO_CAPTURE_ERROR", "GALLERY_SELECTION_ERROR", "PERMISSION_ERROR",
            "GALLERY_PERMISSION_REQUIRED", "GALLERY_PERMISSION_DESCRIPTION",
            "GALLERY_PERMISSION_DENIED", "GALLERY_PERMISSION_DENIED_DESCRIPTION",
            "GALLERY_GRANT_PERMISSION", "GALLERY_BTN_SETTINGS"
        )
        val actual = StringResource.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    // ── when expression exhaustiveness ────────────────────────────────────────

    @Test
    fun whenExpression_coversAllVariants() {
        StringResource.entries.forEach { resource ->
            val label = when (resource) {
                StringResource.CAMERA_PERMISSION_REQUIRED -> "cam_req"
                StringResource.CAMERA_PERMISSION_DESCRIPTION -> "cam_desc"
                StringResource.OPEN_SETTINGS -> "open_set"
                StringResource.CAMERA_PERMISSION_DENIED -> "cam_denied"
                StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "cam_denied_desc"
                StringResource.GRANT_PERMISSION -> "grant"
                StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "perm_denied"
                StringResource.IMAGE_CONFIRMATION_TITLE -> "img_confirm"
                StringResource.ACCEPT_BUTTON -> "accept"
                StringResource.RETRY_BUTTON -> "retry"
                StringResource.SELECT_OPTION_DIALOG_TITLE -> "select_title"
                StringResource.TAKE_PHOTO_OPTION -> "take_photo"
                StringResource.SELECT_FROM_GALLERY_OPTION -> "gallery"
                StringResource.CANCEL_OPTION -> "cancel"
                StringResource.PREVIEW_IMAGE_DESCRIPTION -> "preview"
                StringResource.HD_QUALITY_DESCRIPTION -> "hd"
                StringResource.SD_QUALITY_DESCRIPTION -> "sd"
                StringResource.INVALID_CONTEXT_ERROR -> "invalid_ctx"
                StringResource.PHOTO_CAPTURE_ERROR -> "photo_err"
                StringResource.GALLERY_SELECTION_ERROR -> "gallery_err"
                StringResource.PERMISSION_ERROR -> "perm_err"
                StringResource.GALLERY_PERMISSION_REQUIRED -> "gal_req"
                StringResource.GALLERY_PERMISSION_DESCRIPTION -> "gal_desc"
                StringResource.GALLERY_PERMISSION_DENIED -> "gal_denied"
                StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "gal_denied_desc"
                StringResource.GALLERY_GRANT_PERMISSION -> "gal_grant"
                StringResource.GALLERY_BTN_SETTINGS -> "gal_settings"
            }
            assertTrue(label.isNotEmpty(), "Empty label for $resource")
        }
    }
}
