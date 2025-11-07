package io.github.ismoy.imagepickerkmp.presentation.ui.components

import platform.Foundation.NSURL
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue


fun showPermissionDeniedDialog(
    viewController: UIViewController,
    onDismiss: () -> Unit
) {
    dispatch_async(dispatch_get_main_queue()) {
        val alert = UIAlertController.alertControllerWithTitle(
            title = "Photo Library Access Required",
            message = "We need access to your Photo Library. Please enable it in Settings.",
            preferredStyle = UIAlertControllerStyleAlert
        )

        val settingsAction = UIAlertAction.actionWithTitle(
            title = "Open Settings",
            style = UIAlertActionStyleDefault,
            handler = {
                val settingsUrl = NSURL.URLWithString("app-settings:")
                if (settingsUrl != null) {
                    UIApplication.sharedApplication.openURL(
                        settingsUrl,
                        options = emptyMap<Any?, Any>(),
                        completionHandler = { success ->
                            println(" Settings opened: $success")
                        }
                    )
                }
                onDismiss()
            }
        )

        val cancelAction = UIAlertAction.actionWithTitle(
            title = "Cancel",
            style = UIAlertActionStyleCancel,
            handler = {
                onDismiss()
            }
        )

        alert.addAction(settingsAction)
        alert.addAction(cancelAction)

        viewController.presentViewController(alert, animated = true, completion = null)
    }
}