package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraStateHolder
import io.github.ismoy.imagepickerkmp.scanner.domain.model.FlashMode

@Composable
internal fun ScannerTopBar(
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)?,
    config: ScannerCameraConfig,
    uiExtensions: ScannerUIExtensions = ScannerUIExtensions(),
    stateHolder: ScannerCameraStateHolder
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(config.ui.backgroundHeaderScanner)
            .padding(config.ui.paddingHeaderScanner),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        if (onClose != null) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(config.ui.iconCloseSizeHeaderScanner.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        } else {
            Spacer(modifier = Modifier.size(config.ui.iconCloseSizeHeaderScanner.dp))
        }

        if (config.behavior.enableFlashControl && config.behavior.showFlashButton) {
            if (uiExtensions.customFlashButton != null) {
                uiExtensions.customFlashButton.invoke(stateHolder)
            } else {
                IconButton(
                    onClick = { stateHolder.toggleFlash() },
                    modifier = Modifier
                        .size(config.ui.iconFlashSizeHeaderScanner.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    val isFlashOn = stateHolder.flashMode == FlashMode.ON
                    AnimatedContent(
                        targetState = isFlashOn,
                        transitionSpec = {
                            (scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium)) + fadeIn()) togetherWith
                                    (scaleOut(tween(150)) + fadeOut())
                        },
                        label = "FlashAnimation"
                    ) { flashOn ->
                        Icon(
                            imageVector = if (flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (flashOn) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.size(config.ui.iconFlashSizeHeaderScanner.dp))
        }
    }
}
