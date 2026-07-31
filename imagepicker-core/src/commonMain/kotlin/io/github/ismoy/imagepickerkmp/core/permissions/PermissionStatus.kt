package io.github.ismoy.imagepickerkmp.core.permissions

sealed class PermissionStatus {
    data object Granted : PermissionStatus()
    data object Denied : PermissionStatus()
    data object DeniedPermanently : PermissionStatus()
}
