package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager

actual object CoreServices {
    actual fun permissionManager(): PermissionManager = NoOpPermissionManager()
    actual fun fileSystemManager(subDirectory: String): FileSystemManager = NoOpFileSystemManager(subDirectory)
}
