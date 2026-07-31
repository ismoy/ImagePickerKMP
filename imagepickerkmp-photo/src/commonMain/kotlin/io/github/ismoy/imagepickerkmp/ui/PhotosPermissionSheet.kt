package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosPermissionSheet(
    title: String,
    description: String,
    confirmationButtonText: String,
    onConfirm: () -> Unit,
    cancelButtonText: String,
    onCancel: (() -> Unit)? = null,
    isSettingsDialog: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onCancel?.invoke() },
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)) },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (isSettingsDialog) SettingsIconCombo() else CameraIconCombo()
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = confirmationButtonText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = { onCancel?.invoke() },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    text = cancelButtonText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun CameraIconCombo() {
    val iconColor = MaterialTheme.colorScheme.primary
    val cameraPainter = rememberVectorPainter(Icons.Default.CameraAlt)

    var triggered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cameraScale"
    )

    LaunchedEffect(Unit) {
        delay(280)
        triggered = true
    }

    Canvas(
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val camSize = 52.dp.toPx()
        withTransform({
            scale(scale, scale, pivot = Offset(size.width / 2f, size.height / 2f))
        }) {
            with(cameraPainter) {
                draw(
                    size = Size(camSize, camSize),
                    colorFilter = ColorFilter.tint(iconColor)
                )
            }
        }
    }
}

@Composable
private fun SettingsIconCombo() {
    val iconColor = MaterialTheme.colorScheme.primary
    val shieldPainter = rememberVectorPainter(Icons.Default.Security)
    val settingsPainter = rememberVectorPainter(Icons.Outlined.Settings)

    var triggered by remember { mutableStateOf(false) }
    val gearScale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gearScale"
    )
    val gearRotation by animateFloatAsState(
        targetValue = if (triggered) 0f else 90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gearRotation"
    )

    LaunchedEffect(Unit) {
        delay(280)
        triggered = true
    }

    Canvas(
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    ) {
        val shieldSize = 52.dp.toPx()
        val gearSize = 31.dp.toPx()
        val gap = 3.dp.toPx()

        with(shieldPainter) {
            draw(
                size = Size(shieldSize, shieldSize),
                colorFilter = ColorFilter.tint(iconColor)
            )
        }

        drawCircle(
            color = Color.Black,
            radius = ((gearSize / 2f) + gap) * gearScale,
            center = Offset(size.width - gearSize / 2f, size.height - gearSize / 2f),
            blendMode = BlendMode.DstOut
        )

        translate(left = size.width - gearSize, top = size.height - gearSize) {
            withTransform({
                scale(gearScale, gearScale, pivot = Offset(gearSize / 2f, gearSize / 2f))
                rotate(gearRotation, pivot = Offset(gearSize / 2f, gearSize / 2f))
            }) {
                with(settingsPainter) {
                    draw(
                        size = Size(gearSize, gearSize),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }
        }
    }
}
