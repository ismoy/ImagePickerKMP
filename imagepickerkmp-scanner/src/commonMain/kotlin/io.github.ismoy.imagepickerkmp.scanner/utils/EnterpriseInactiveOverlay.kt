package io.github.ismoy.imagepickerkmp.scanner.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ismoy.imagepickerkmp.scanner.camera.config.EnterpriseOverlayConfig

@Composable
fun EnterpriseInactiveOverlay(
    onTap: () -> Unit,
    tapText: String,
    config: EnterpriseOverlayConfig
) {
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.surface, bgColor),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .clickable(onClick = onTap)
            .drawBehind {
                val strokeWidth = 1f
                val spacing = 12.dp.toPx()
                for (i in 0 until (size.height / spacing).toInt()) {
                    val y = i * spacing
                    drawLine(
                        color = primaryColor.copy(alpha = 0.03f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (config.title != null) {
                        Text(
                            text = config.title,
                            color = onSurfaceColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                if (config.tag != null) {
                    Box(
                        modifier = Modifier
                            .background(primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(1.dp, primaryColor.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = config.tag.uppercase(),
                            color = primaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(surfaceColor, RoundedCornerShape(24.dp))
                    .border(1.dp, outlineVariantColor, RoundedCornerShape(24.dp))
                    .drawBehind {
                        val bracketSize = 24.dp.toPx()
                        val stroke = Stroke(width = 2.dp.toPx())
                        val color = primaryColor.copy(alpha = 0.8f)
                        drawLine(color, Offset(0f, 0f), Offset(bracketSize, 0f), strokeWidth = stroke.width)
                        drawLine(color, Offset(0f, 0f), Offset(0f, bracketSize), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width, 0f), Offset(size.width - bracketSize, 0f), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width, 0f), Offset(size.width, bracketSize), strokeWidth = stroke.width)
                        drawLine(color, Offset(0f, size.height), Offset(bracketSize, size.height), strokeWidth = stroke.width)
                        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - bracketSize), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width, size.height), Offset(size.width - bracketSize, size.height), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - bracketSize), strokeWidth = stroke.width)
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.3f),
                            radius = size.width / 2.2f,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 10.dp.toPx()))
                            )
                        )
                        drawCircle(
                            color = primaryContainer.copy(alpha = 0.2f),
                            radius = size.width / 2.5f,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20.dp.toPx(), 40.dp.toPx()))
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(24.dp).background(primaryColor, CircleShape))
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (config.statusLabel != null || config.infoLine1Label != null || config.infoLine2Label != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(surfaceColor, RoundedCornerShape(12.dp))
                        .border(1.dp, outlineVariantColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (config.statusLabel != null && config.statusValue != null) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(config.statusLabel.uppercase(), color = onSurfaceVariantColor, fontSize = 12.sp, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).background(primaryColor, CircleShape).padding(end = 4.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(config.statusValue, color = primaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (config.infoLine1Label != null && config.infoLine1Value != null) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(config.infoLine1Label.uppercase(), color = onSurfaceVariantColor, fontSize = 12.sp, letterSpacing = 1.sp)
                            Text(config.infoLine1Value, color = onSurfaceColor, fontSize = 12.sp)
                        }
                    }
                    if (config.infoLine2Label != null && config.infoLine2Value != null) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(config.infoLine2Label.uppercase(), color = onSurfaceVariantColor, fontSize = 12.sp, letterSpacing = 1.sp)
                            Text(config.infoLine2Value, color = onSurfaceColor, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 32.dp)) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = primaryColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(surfaceColor.copy(alpha = 0.5f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tapText.uppercase(),
                        color = onSurfaceVariantColor,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    config.footerLeftLines.forEach { line ->
                        Text(line, color = onSurfaceVariantColor.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    config.footerRightLines.forEach { line ->
                        Text(line, color = onSurfaceVariantColor.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}
