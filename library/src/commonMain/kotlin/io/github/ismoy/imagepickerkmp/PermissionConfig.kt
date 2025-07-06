package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.Constant.BTN_DIALOG_CONFIG
import io.github.ismoy.imagepickerkmp.Constant.BTN_DIALOG_DENIED
import io.github.ismoy.imagepickerkmp.Constant.DESCRIPTION_DIALOG_CONFIG
import io.github.ismoy.imagepickerkmp.Constant.DESCRIPTION_DIALOG_DENIED
import io.github.ismoy.imagepickerkmp.Constant.TITLE_DIALOG_CONFIG
import io.github.ismoy.imagepickerkmp.Constant.TITLE_DIALOG_DENIED

data class PermissionConfig(
    val titleDialogConfig:String = TITLE_DIALOG_CONFIG,
    val descriptionDialogConfig:String = DESCRIPTION_DIALOG_CONFIG,
    val btnDialogConfig:String = BTN_DIALOG_CONFIG,
    val titleDialogDenied:String = TITLE_DIALOG_DENIED,
    val descriptionDialogDenied:String = DESCRIPTION_DIALOG_DENIED,
    val btnDialogDenied:String = BTN_DIALOG_DENIED
)