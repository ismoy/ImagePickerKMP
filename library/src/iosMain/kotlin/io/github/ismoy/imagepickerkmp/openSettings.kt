
package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.SYSTEM_VERSION_10
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIDevice

 fun openSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
        if (UIDevice.currentDevice.systemVersion.toDouble() >= SYSTEM_VERSION_10) {
            UIApplication.sharedApplication.openURL(
                settingsUrl,
                options = mapOf<Any?, Any?>(),
                completionHandler = null
            )
        } else {
            UIApplication.sharedApplication.openURL(settingsUrl)
        }
    }
}
