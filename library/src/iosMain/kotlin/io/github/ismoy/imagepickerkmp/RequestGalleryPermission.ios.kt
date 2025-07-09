package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.*
import platform.Photos.*

@Composable
fun RequestGalleryPermission(
    onGranted: () -> Unit,
    onLimited: () -> Unit,
    onDenied: () -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val status = PHPhotoLibrary.authorizationStatusForAccessLevel(PHAccessLevelReadWrite)
        if (status == PHAuthorizationStatusAuthorized) {
            onGranted()
        } else if (status == PHAuthorizationStatusLimited) {
            onLimited()
        } else if (status == PHAuthorizationStatusNotDetermined) {
            PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelReadWrite) { newStatus ->
                when (newStatus) {
                    PHAuthorizationStatusAuthorized -> onGranted()
                    PHAuthorizationStatusLimited -> onLimited()
                    else -> onDenied()
                }
            }
        } else {
            onDenied()
        }
        checked = true
    }
}
