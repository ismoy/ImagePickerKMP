package io.github.ismoy.imagepickerkmp.scanner

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
actual fun getLocalContext(): Any? = LocalContext.current

@Composable
actual fun getLocalLifecycleOwner(): Any? = LocalLifecycleOwner.current

actual fun openAppSettings(context: Any?) {
    val androidContext = context as? android.content.Context ?: return
    val intent = android.content.Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", androidContext.packageName, null)
        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    androidContext.startActivity(intent)
}
