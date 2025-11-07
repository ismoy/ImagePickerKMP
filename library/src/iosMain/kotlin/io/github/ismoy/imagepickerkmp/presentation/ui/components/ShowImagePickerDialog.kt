package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert

@Composable
 fun showImagePickerDialog(
    config: ImagePickerConfig,
    onTakePhoto: () -> Unit,
    onSelectFromGallery: () -> Unit,
    onCancel: () -> Unit
) {
    LaunchedEffect(Unit) {
        val rootVC = ViewControllerProvider.getRootViewController() ?: return@LaunchedEffect
        val alert = UIAlertController.alertControllerWithTitle(
            title = config.dialogTitle,
            message = null,
            preferredStyle = UIAlertControllerStyleAlert
        )

        alert.addAction(
            UIAlertAction.actionWithTitle(config.takePhotoText, 0) { onTakePhoto() }
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(config.selectFromGalleryText, 0) { onSelectFromGallery() }
        )
        alert.addAction(
            UIAlertAction.actionWithTitle(config.cancelText, 1) { onCancel() }
        )

        rootVC.presentViewController(alert, animated = true, completion = null)
    }
}