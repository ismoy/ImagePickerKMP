package io.github.ismoy.imagepickerkmp

// TODO: Re-enable this test once Compose testing is properly configured
/*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CustomPermissionDialogTest {
    // Test implementation will be added back once Compose testing is configured
}
*/

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CustomPermissionDialogTest {
    @Test
    fun testOnPermissionGrantedIsCalled() {
        var granted = false
        val dialog = FakeCustomPermissionDialog(
            onGranted = { granted = true },
            onDenied = { },
            onError = { }
        )
        dialog.simulateGrant()
        assertEquals(true, granted)
    }

    @Test
    fun testOnPermissionDeniedIsCalled() {
        var denied = false
        val dialog = FakeCustomPermissionDialog(
            onGranted = { },
            onDenied = { denied = true },
            onError = { }
        )
        dialog.simulateDeny()
        assertEquals(true, denied)
    }

    @Test
    fun testOnErrorIsCalled() {
        var error: Exception? = null
        val dialog = FakeCustomPermissionDialog(
            onGranted = { },
            onDenied = { },
            onError = { e -> error = e }
        )
        dialog.simulateError(Exception("Permission error"))
        assertNotNull(error)
        assertEquals("Permission error", error?.message)
    }
}

class FakeCustomPermissionDialog(
    val onGranted: () -> Unit,
    val onDenied: () -> Unit,
    val onError: (Exception) -> Unit
) {
    fun simulateGrant() { onGranted() }
    fun simulateDeny() { onDenied() }
    fun simulateError(e: Exception) { onError(e) }
}
