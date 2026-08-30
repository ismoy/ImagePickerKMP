package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
 fun ScannerIconCombo() {
    val iconColor = MaterialTheme.colorScheme.primary
    val cameraPainter = rememberVectorPainter(Icons.Default.QrCodeScanner)

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
        delay(280.milliseconds)
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
