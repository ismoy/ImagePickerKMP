package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.filesystem.PlatformFile
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionStatus
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionType

internal class NoOpPermissionManager : PermissionManager {
    override suspend fun checkPermission(type: PermissionType) = PermissionStatus.Granted
    override suspend fun requestPermission(type: PermissionType) = PermissionStatus.Granted
}

internal class NoOpFileSystemManager(private val subDirectory: String) : FileSystemManager {
    override suspend fun createTempFile(prefix: String, suffix: String): PlatformFile =
        PlatformFile("$subDirectory/$prefix${suffix.ifEmpty { ".tmp" }}")
    override suspend fun getCacheDirectory(): PlatformFile = PlatformFile(subDirectory)
    override suspend fun clearCache() {}
}
