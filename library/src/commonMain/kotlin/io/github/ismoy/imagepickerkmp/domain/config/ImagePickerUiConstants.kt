package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @author ismoy
 */
object ImagePickerUiConstants {
    val FlashToggleBackgroundColor = Color(0xAA444444)
    const val FlashToggleCornerRadius = 50
    val FlashToggleHorizontalPadding = 30.dp
    val FlashToggleVerticalPadding = 8.dp

    val ConfirmationCardMaxWidth = 400.dp
    val ConfirmationCardCornerRadius = 32.dp
    const val ConfirmationCardImageAspectRatio = 1f
    val ConfirmationCardPadding = 24.dp
    val ConfirmationCardSpacerHeight = 24.dp
    val ConfirmationCardButtonHeight = 48.dp
    val ConfirmationCardButtonSpacing = 16.dp
    val ConfirmationCardTitleFontSize = 22.sp
    val ConfirmationCardTitleColor = Color(0xFFCBD5E1)
    val ConfirmationCardBackgroundColor = Color(0xFF2D3748)
    val BackgroundColor = Color(0xFFF5F5F5)
    val ConfirmationCardIconColor = Color.White
    val ConfirmationCardButtonSize = 48.dp
    val ConfirmationCardRetryButtonColor = Color(0xFFE53935)
    val ConfirmationCardAcceptButtonColor = Color(0xFF43A047)
    val ConfirmationCardButtonIconPadding = 4.dp
    val ConfirmationCardDialogBackground = Color(0xFF1A202C)
    val ConfirmationCardDialogHorizontalPadding = 16.dp
    val ConfirmationCardButtonTextFontSize = 16.sp
    val ConfirmationCardButtonTextFontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    val DefaultCardElevation = 16.dp
    const val ORIENTATION_ROTATE_90 = 90f
    const val ORIENTATION_ROTATE_180 = 180f
    const val ORIENTATION_ROTATE_270 = 270f
    const val ORIENTATION_FLIP_HORIZONTAL_X = -1f
    const val ORIENTATION_FLIP_HORIZONTAL_Y = 1f
    const val ORIENTATION_FLIP_VERTICAL_X = 1f
    const val ORIENTATION_FLIP_VERTICAL_Y = -1f
    const val SYSTEM_VERSION_10 = 10.0
    const val DELAY_TO_TAKE_PHOTO = 1L
    const val SELECTION_LIMIT = 30L
}
