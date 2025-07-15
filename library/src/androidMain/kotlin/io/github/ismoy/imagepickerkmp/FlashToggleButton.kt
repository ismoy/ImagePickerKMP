package io.github.ismoy.imagepickerkmp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.outlined.FlashAuto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A toggle button that switches between flash modes: AUTO, ON, OFF.
 *
 * @param flashMode The current flash mode.
 * @param iconColor The color to use for the flash icon.
 * @param flashIcon Optional override icon to display instead of the default.
 * @param onToggle Callback triggered when the user taps the button.
 */
@Composable
fun FlashToggleButton(
    flashMode: CameraController.FlashMode,
    iconColor: Color,
    flashIcon: ImageVector? = null,
    onToggle: () -> Unit
) {
    val icon = flashIcon ?: when (flashMode) {
        CameraController.FlashMode.AUTO -> Icons.Outlined.FlashAuto
        CameraController.FlashMode.ON -> Icons.Default.FlashOn
        CameraController.FlashMode.OFF -> Icons.Default.FlashOff
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.TopCenter)
                .background(
                    ImagePickerUiConstants.FlashToggleBackgroundColor,
                    shape = RoundedCornerShape(ImagePickerUiConstants.FlashToggleCornerRadius)
                )
                .padding(
                    horizontal = ImagePickerUiConstants.FlashToggleHorizontalPadding,
                    vertical = ImagePickerUiConstants.FlashToggleVerticalPadding
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = "Flash mode", tint = iconColor)
        }
    }
}
