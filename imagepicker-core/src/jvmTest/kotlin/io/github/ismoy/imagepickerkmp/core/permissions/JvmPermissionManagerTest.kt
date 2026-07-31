package io.github.ismoy.imagepickerkmp.core.permissions

import io.github.ismoy.imagepickerkmp.core.CoreServices
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmPermissionManagerTest {

    private val manager: PermissionManager = CoreServices.permissionManager()

    @Test
    fun checkPermission_camera_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.checkPermission(PermissionType.Camera))
    }

    @Test
    fun checkPermission_gallery_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.checkPermission(PermissionType.Gallery))
    }

    @Test
    fun checkPermission_storage_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.checkPermission(PermissionType.Storage))
    }

    @Test
    fun checkPermission_microphone_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.checkPermission(PermissionType.Microphone))
    }

    @Test
    fun requestPermission_camera_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.requestPermission(PermissionType.Camera))
    }

    @Test
    fun requestPermission_gallery_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.requestPermission(PermissionType.Gallery))
    }

    @Test
    fun requestPermission_storage_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.requestPermission(PermissionType.Storage))
    }

    @Test
    fun requestPermission_microphone_returnsGranted() = runTest {
        assertEquals(PermissionStatus.Granted, manager.requestPermission(PermissionType.Microphone))
    }

    @Test
    fun checkAndRequest_sameResult_allTypes() = runTest {
        PermissionType::class.sealedSubclasses
        listOf(
            PermissionType.Camera,
            PermissionType.Gallery,
            PermissionType.Storage,
            PermissionType.Microphone
        ).forEach { type ->
            val checked = manager.checkPermission(type)
            val requested = manager.requestPermission(type)
            assertEquals(checked, requested, "check and request must agree for $type")
        }
    }
}
