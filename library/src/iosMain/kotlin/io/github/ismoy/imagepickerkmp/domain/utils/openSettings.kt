package io.github.ismoy.imagepickerkmp.domain.utils

import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SYSTEM_VERSION_10
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIDevice
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private fun isIOSVersionAtLeast(version: Double): Boolean {
    return try {
        val systemVersion = UIDevice.currentDevice.systemVersion
        val versionParts = systemVersion.split(".")
        if (versionParts.isNotEmpty()) {
            val majorMinor = if (versionParts.size >= 2) {
                "${versionParts[0]}.${versionParts[1]}"
            } else {
                versionParts[0]
            }
            majorMinor.toDoubleOrNull()?.let { it >= version } ?: false
        } else {
            false
        }
    } catch (e: Exception) {
        true
    }
}

fun openSettings() {
    dispatch_async(dispatch_get_main_queue()) {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            if (isIOSVersionAtLeast(SYSTEM_VERSION_10)) {
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
}
