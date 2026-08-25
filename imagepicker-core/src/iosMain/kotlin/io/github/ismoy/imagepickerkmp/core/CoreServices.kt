package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.filesystem.IosFileSystemManager
import io.github.ismoy.imagepickerkmp.core.permissions.IosPermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager

actual object CoreServices {
    actual fun permissionManager(): PermissionManager = IosPermissionManager()
    actual fun fileSystemManager(subDirectory: String): FileSystemManager =
        IosFileSystemManager(subDirectory)
}
