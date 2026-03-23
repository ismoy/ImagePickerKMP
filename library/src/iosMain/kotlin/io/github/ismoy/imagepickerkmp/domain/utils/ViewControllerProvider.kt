package io.github.ismoy.imagepickerkmp.domain.utils

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

internal object ViewControllerProvider {
    fun getRootViewController(): UIViewController? =
         UIApplication.sharedApplication.keyWindow?.rootViewController
}
