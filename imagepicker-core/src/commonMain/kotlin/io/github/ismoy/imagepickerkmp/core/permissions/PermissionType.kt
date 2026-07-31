package io.github.ismoy.imagepickerkmp.core.permissions

sealed interface PermissionType {
    data object Camera : PermissionType
    data object Gallery : PermissionType
    data object Storage : PermissionType
    data object Microphone : PermissionType
}
