package io.github.ismoy.imagepickerkmp.presentation.ui.components.ocr

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ismoy.imagepickerkmp.features.ocr.model.ExtractionIndicators
import kotlinx.coroutines.delay

@Composable
 fun ProcessingContent(providerName: String, extractionIndicators: ExtractionIndicators) {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF8BC34A),
        Color(0xFF4CAF50)
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = colors.map { it.copy(alpha = 0.2f) }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((60 * scale).dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🧠",
                fontSize = 32.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    var dotCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotCount = (dotCount + 1) % 4
        }
    }

    Text(
        text = "$providerName is analyzing${"."
            .repeat(dotCount)}",
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        ),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "Extracting text and structured information",
        style = TextStyle(
            fontSize = 14.sp,
            color = Color.Gray
        ),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(20.dp))

    AnimatedProgressBar(progress = 0.7f)

    Spacer(modifier = Modifier.height(16.dp))

    OCRFeatureIndicators( extractionIndicators)
}