package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.filesystem.JvmFileSystemManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.JvmPermissionManager

actual object CoreServices {
    actual fun permissionManager(): PermissionManager = JvmPermissionManager()
    actual fun fileSystemManager(subDirectory: String): FileSystemManager =
        JvmFileSystemManager(subDirectory)
}
