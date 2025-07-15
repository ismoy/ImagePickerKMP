
package io.github.ismoy.imagepickerkmp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
actual fun RequestCameraPermission(
    dialogConfig: CameraPermissionDialogConfig,
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit,
    customPermissionHandler: (() -> Unit)?
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var permissionDeniedCount by remember { mutableIntStateOf(0) }
    var hasCalledPermanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted){
            onResult(true)
        }else{
            permissionDeniedCount++
            when{
               permissionDeniedCount >= 2 -> {
                   permissionDeniedPermanently = true
                   showRationale = false
               }
                else -> {
                    showRationale = true
                    permissionDeniedPermanently = false
                }
            }
        }
    }

    LaunchedEffect(Unit){
        val currentPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        when (currentPermission){
            PackageManager.PERMISSION_GRANTED -> {
                onResult(true)
            }
            PackageManager.PERMISSION_DENIED -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    if (showRationale) {
        if (dialogConfig.customDeniedDialog != null) {
            dialogConfig.customDeniedDialog {
                showRationale = false
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                onConfirm = {
                    showRationale = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }
    }

    if (permissionDeniedPermanently && !hasCalledPermanentlyDenied){
        if (dialogConfig.customSettingsDialog != null){
            dialogConfig.customSettingsDialog {

                openAppSettings(context)
                hasCalledPermanentlyDenied = true
                onPermissionPermanentlyDenied()
            }
        }
        else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogConfig,
                description = dialogConfig.descriptionDialogConfig,
                confirmationButtonText = dialogConfig.btnDialogConfig,
                onConfirm = {

                    openAppSettings(context)
                    hasCalledPermanentlyDenied = true
                    onPermissionPermanentlyDenied()
                }
            )
        }
    }
}
