package io.github.ismoy.imagepickerkmp.data.utils

import android.content.pm.PackageManager
import junit.framework.TestCase

class PermissionUtilsComprehensiveTest : TestCase() {

    fun testCameraPermissionConstant() {
        val permission = android.Manifest.permission.CAMERA
        assertEquals("android.permission.CAMERA", permission)
    }

    fun testReadStoragePermissionConstant() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        assertEquals("android.permission.READ_EXTERNAL_STORAGE", permission)
    }

    fun testWriteStoragePermissionConstant() {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        assertEquals("android.permission.WRITE_EXTERNAL_STORAGE", permission)
    }

    fun testPermissionConstants() {
        // Verify Android permission constants
        assertEquals("android.permission.CAMERA", android.Manifest.permission.CAMERA)
        assertEquals("android.permission.READ_EXTERNAL_STORAGE", android.Manifest.permission.READ_EXTERNAL_STORAGE)
        assertEquals("android.permission.WRITE_EXTERNAL_STORAGE", android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun testPermissionGrantedValues() {
        assertEquals(0, PackageManager.PERMISSION_GRANTED)
        assertEquals(-1, PackageManager.PERMISSION_DENIED)
    }
}
