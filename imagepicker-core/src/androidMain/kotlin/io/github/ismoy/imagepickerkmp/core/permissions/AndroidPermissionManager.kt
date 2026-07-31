package io.github.ismoy.imagepickerkmp.core.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal class AndroidPermissionManager(
    private val context: Context,
    private val activityProvider: () -> Activity? = { null }
) : PermissionManager {

    override suspend fun checkPermission(type: PermissionType): PermissionStatus {
        val permission = androidPermissionFor(type) ?: return PermissionStatus.Granted
        val granted = ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) return PermissionStatus.Granted
        val activity = activityProvider()
        return when {
            activity == null -> PermissionStatus.Denied
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) ->
                PermissionStatus.Denied
            else -> PermissionStatus.DeniedPermanently
        }
    }

    override suspend fun requestPermission(type: PermissionType): PermissionStatus =
        checkPermission(type)

    private fun androidPermissionFor(type: PermissionType): String? = when (type) {
        is PermissionType.Camera -> Manifest.permission.CAMERA
        is PermissionType.Gallery ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE
        is PermissionType.Storage ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) null
            else Manifest.permission.READ_EXTERNAL_STORAGE
        is PermissionType.Microphone -> Manifest.permission.RECORD_AUDIO
    }
}
