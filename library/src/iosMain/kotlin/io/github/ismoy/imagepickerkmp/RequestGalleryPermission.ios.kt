package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.Photos.PHAccessLevelReadWrite
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary

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
