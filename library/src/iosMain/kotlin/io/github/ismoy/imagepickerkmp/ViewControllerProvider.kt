package io.github.ismoy.imagepickerkmp

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
object ViewControllerProvider {
    fun getRootViewController(): UIViewController? {
        return UIApplication.sharedApplication.keyWindow?.rootViewController
    }
}