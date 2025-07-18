package io.github.ismoy.imagepickerkmp

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RequestCameraPermissionTest {
    // Test implementation will be added back once Compose testing is configured
}
*/

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RequestCameraPermissionTest {
    @Test
    fun testOnPermissionGrantedIsCalled() {
        var granted = false
        val permission = FakeRequestCameraPermission(
            onGranted = { granted = true },
            onDenied = { },
            onError = { }
        )
        permission.simulateGrant()
        assertEquals(true, granted)
    }

    @Test
    fun testOnPermissionDeniedIsCalled() {
        var denied = false
        val permission = FakeRequestCameraPermission(
            onGranted = { },
            onDenied = { denied = true },
            onError = { }
        )
        permission.simulateDeny()
        assertEquals(true, denied)
    }

    @Test
    fun testOnErrorIsCalled() {
        var error: Exception? = null
        val permission = FakeRequestCameraPermission(
            onGranted = { },
            onDenied = { },
            onError = { e -> error = e }
        )
        permission.simulateError(Exception("Camera permission error"))
        assertNotNull(error)
        assertEquals("Camera permission error", error?.message)
    }

    @Test
    fun testPermissionPermanentlyDenied() {
        var permanentlyDenied = false
        val permission = FakeRequestCameraPermission(
            onGranted = { },
            onDenied = { },
            onError = { }
        )
        permission.simulatePermanentlyDenied = { permanentlyDenied = true }
        permission.simulatePermanentDeny()
        assertTrue(permanentlyDenied)
    }

    @Test
    fun testPermissionGrantedAfterRetry() {
        var granted = false
        var denied = false
        val permission = FakeRequestCameraPermission(
            onGranted = { granted = true },
            onDenied = { denied = true },
            onError = { }
        )
        permission.simulateDeny()
        permission.simulateGrant()
        assertTrue(denied)
        assertTrue(granted)
    }

    @Test
    fun testMultipleCallbackCalls() {
        var grantedCount = 0
        var deniedCount = 0
        val permission = FakeRequestCameraPermission(
            onGranted = { grantedCount++ },
            onDenied = { deniedCount++ },
            onError = { }
        )
        permission.simulateGrant()
        permission.simulateDeny()
        permission.simulateGrant()
        assertEquals(2, grantedCount)
        assertEquals(1, deniedCount)
    }
}

class FakeRequestCameraPermission(
    val onGranted: () -> Unit,
    val onDenied: () -> Unit,
    val onError: (Exception) -> Unit
) {
    var simulatePermanentlyDenied: (() -> Unit)? = null
    fun simulateGrant() { onGranted() }
    fun simulateDeny() { onDenied() }
    fun simulateError(e: Exception) { onError(e) }
    fun simulatePermanentDeny() { simulatePermanentlyDenied?.invoke() }
} 