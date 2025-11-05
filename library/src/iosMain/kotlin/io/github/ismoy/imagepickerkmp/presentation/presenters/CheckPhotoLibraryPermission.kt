package io.github.ismoy.imagepickerkmp.presentation.presenters

import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

 fun checkPhotoLibraryPermission(
    onAuthorized: () -> Unit,
    onDenied: () -> Unit
) {
    val currentStatus = PHPhotoLibrary.authorizationStatus()

    when (currentStatus) {
        PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> {
            onAuthorized()
        }
        PHAuthorizationStatusNotDetermined -> {
            PHPhotoLibrary.requestAuthorization { status ->
                dispatch_async(dispatch_get_main_queue()) {
                    when (status) {
                        PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> {
                            onAuthorized()
                        }
                        else -> {
                            onDenied()
                        }
                    }
                }
            }
        }
        PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> {
            onDenied()
        }
        else -> {
            onDenied()
        }
    }
}