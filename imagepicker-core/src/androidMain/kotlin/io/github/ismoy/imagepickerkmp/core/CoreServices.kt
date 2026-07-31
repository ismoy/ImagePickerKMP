package io.github.ismoy.imagepickerkmp.core

import io.github.ismoy.imagepickerkmp.core.filesystem.AndroidFileSystemManager
import io.github.ismoy.imagepickerkmp.core.filesystem.FileSystemManager
import io.github.ismoy.imagepickerkmp.core.permissions.AndroidPermissionManager
import io.github.ismoy.imagepickerkmp.core.permissions.PermissionManager

actual object CoreServices {
    actual fun permissionManager(): PermissionManager =
        AndroidPermissionManager(CoreServicesHolder.requireApplication())

    fun permissionManager(activityProvider: () -> android.app.Activity?): PermissionManager =
        AndroidPermissionManager(CoreServicesHolder.requireApplication(), activityProvider)

    actual fun fileSystemManager(subDirectory: String): FileSystemManager =
        AndroidFileSystemManager(CoreServicesHolder.requireApplication(), subDirectory)

    fun applicationContext(): android.content.Context =
        CoreServicesHolder.requireApplication().applicationContext
}
