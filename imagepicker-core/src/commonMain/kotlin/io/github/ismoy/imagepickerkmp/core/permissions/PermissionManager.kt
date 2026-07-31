package io.github.ismoy.imagepickerkmp.core.permissions

interface PermissionManager {
    suspend fun checkPermission(type: PermissionType): PermissionStatus
    suspend fun requestPermission(type: PermissionType): PermissionStatus
}
