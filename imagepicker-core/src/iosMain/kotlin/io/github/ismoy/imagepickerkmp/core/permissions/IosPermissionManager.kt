package io.github.ismoy.imagepickerkmp.core.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import kotlin.coroutines.resume

internal class IosPermissionManager : PermissionManager {

    override suspend fun checkPermission(type: PermissionType): PermissionStatus = when (type) {
        is PermissionType.Camera -> mapCameraStatus(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        )
        is PermissionType.Gallery -> mapPhotoStatus(PHPhotoLibrary.authorizationStatus())
        is PermissionType.Storage -> PermissionStatus.Granted
        is PermissionType.Microphone -> mapCameraStatus(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)
        )
    }

    override suspend fun requestPermission(type: PermissionType): PermissionStatus = when (type) {
        is PermissionType.Camera -> suspendCancellableCoroutine { cont ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                cont.resume(
                    if (granted) PermissionStatus.Granted else PermissionStatus.DeniedPermanently
                )
            }
        }
        is PermissionType.Gallery -> suspendCancellableCoroutine { cont ->
            PHPhotoLibrary.requestAuthorization { status ->
                cont.resume(mapPhotoStatus(status))
            }
        }
        is PermissionType.Storage -> PermissionStatus.Granted
        is PermissionType.Microphone -> suspendCancellableCoroutine { cont ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeAudio) { granted ->
                cont.resume(
                    if (granted) PermissionStatus.Granted else PermissionStatus.DeniedPermanently
                )
            }
        }
    }

    private fun mapCameraStatus(status: Long): PermissionStatus = when (status) {
        AVAuthorizationStatusAuthorized -> PermissionStatus.Granted
        AVAuthorizationStatusNotDetermined -> PermissionStatus.Denied
        AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> PermissionStatus.DeniedPermanently
        else -> PermissionStatus.DeniedPermanently
    }

    private fun mapPhotoStatus(status: Long): PermissionStatus = when (status) {
        PHAuthorizationStatusAuthorized -> PermissionStatus.Granted
        PHAuthorizationStatusNotDetermined -> PermissionStatus.Denied
        PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> PermissionStatus.DeniedPermanently
        else -> PermissionStatus.DeniedPermanently
    }
}
