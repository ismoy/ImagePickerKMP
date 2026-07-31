package io.github.ismoy.imagepickerkmp.core.permissions

internal class JvmPermissionManager : PermissionManager {
    override suspend fun checkPermission(type: PermissionType) = PermissionStatus.Granted
    override suspend fun requestPermission(type: PermissionType) = PermissionStatus.Granted
}
