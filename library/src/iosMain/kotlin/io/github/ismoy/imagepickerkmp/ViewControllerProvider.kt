package io.github.ismoy.imagepickerkmp

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

/**
 * Provides access to the root view controller in an iOS application.
 *
 * Used to obtain the root view controller for presenting camera or gallery interfaces.
 */
object ViewControllerProvider {
    fun getRootViewController(): UIViewController? =
         UIApplication.sharedApplication.keyWindow?.rootViewController
}
