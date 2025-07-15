
package io.github.ismoy.imagepickerkmp

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openAppSettings(context: Context){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = android.net.Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
