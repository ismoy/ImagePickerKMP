package io.github.ismoy.imagepickerkmp.picker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.PhotosUI.PHPickerConfiguration
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType

internal actual object PlatformPrewarmer {
    private var isPrewarmed = false

    actual fun prewarm() {
        if (isPrewarmed) return
        isPrewarmed = true

        CoroutineScope(Dispatchers.Default).launch {
            try {
                UIImagePickerController.isSourceTypeAvailable(
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                )
            } catch (_: Exception) {}

            try {
                PHPickerConfiguration()
            } catch (_: Exception) {}
        }
    }
}
