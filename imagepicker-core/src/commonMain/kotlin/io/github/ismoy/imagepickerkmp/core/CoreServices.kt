package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager

expect object CoreServices {
    fun permissionManager(): PermissionManager
    fun fileSystemManager(subDirectory: String = "core_temp"): FileSystemManager
}
