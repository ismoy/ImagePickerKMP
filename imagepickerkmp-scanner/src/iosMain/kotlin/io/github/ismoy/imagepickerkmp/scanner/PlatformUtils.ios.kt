package io.github.ismoy.imagepickerkmp.scanner

import androidx.compose.runtime.Composable

@Composable
actual fun getLocalContext(): Any? = null

@Composable
actual fun getLocalLifecycleOwner(): Any? = null

actual fun openAppSettings(context: Any?) {
    platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
        val settingsUrl = platform.Foundation.NSURL.URLWithString(platform.UIKit.UIApplicationOpenSettingsURLString)
        if (settingsUrl != null && platform.UIKit.UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            platform.UIKit.UIApplication.sharedApplication.openURL(
                settingsUrl,
                options = emptyMap<Any?, Any?>(),
                completionHandler = null
            )
        }
    }
}