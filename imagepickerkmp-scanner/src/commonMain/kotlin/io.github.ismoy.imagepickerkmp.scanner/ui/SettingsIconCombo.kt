package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
 fun SettingsIconCombo() {
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
        delay(280.milliseconds)
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