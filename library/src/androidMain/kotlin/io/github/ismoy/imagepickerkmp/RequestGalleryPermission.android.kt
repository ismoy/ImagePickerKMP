package io.github.ismoy.imagepickerkmp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
actual fun RequestGalleryPermission(
    onGranted: () -> Unit,
    onLimited: () -> Unit,
    onDenied: () -> Unit,
    customDeniedDialog: (@Composable (() -> Unit))?,
    dialogConfig: CameraPermissionDialogConfig,
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionRequested = true
        if (isGranted) {
            onGranted()
        } else {
            if (context is ComponentActivity &&
                !ActivityCompat.shouldShowRequestPermissionRationale(context, permission)
            ) {
                showSettingsDialog = true
            } else {
                showRationale = true
            }
        }
    }

    LaunchedEffect(Unit) {
        if (context is ComponentActivity) {
            val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                onGranted()
            } else if (!permissionRequested) {
                launcher.launch(permission)
            }
        } else {
            onDenied()
        }
    }

    if (showRationale) {
        if (customDeniedDialog != null) {
            customDeniedDialog()
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogDenied ,
                description = dialogConfig.descriptionDialogDenied,
                confirmationButtonText = dialogConfig.btnDialogDenied,
                onConfirm = {
                    showRationale = false
                    launcher.launch(permission)
                }
            )
        }
    }

    if (showSettingsDialog) {
        if (customDeniedDialog != null) {
            customDeniedDialog()
        } else {
            CustomPermissionDialog(
                title = dialogConfig.titleDialogConfig,
                description = dialogConfig.descriptionDialogConfig,
                confirmationButtonText = dialogConfig.btnDialogConfig,
                onConfirm = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:" + context.packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    onDenied()
                }
            )
        }
    }
}
