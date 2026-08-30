package io.github.ismoy.imagepickerkmp.scanner.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerOverlayStyle
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance

@Composable
fun ActiveScanningOverlay(
    watermark: String,
    scanDistance: CameraPositionDistance = CameraPositionDistance.TOO_FAR,
    tooFarColor: Color = Color.Red,
    tooCloseColor: Color ,
    tooOptimalColor: Color,
    tooFarText:String,
    tooCloseText:String,
    tooOptimalText:String,
    showScanLine: Boolean = true,
    scanLineColor: Color = Color.Red,
    overlayCornerRadius: Dp = 0.dp,
    overlayStyle: ScannerOverlayStyle = ScannerOverlayStyle.ANIMATED_LINE
) {

    val frameColor = when (scanDistance) {
        CameraPositionDistance.TOO_FAR -> tooFarColor
        CameraPositionDistance.TOO_CLOSE -> tooCloseColor
        CameraPositionDistance.OPTIMAL -> tooOptimalColor
        CameraPositionDistance.UNKNOWN -> tooFarColor
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val transition = rememberInfiniteTransition(label = "")
        val offsetFloat by transition.animateFloat(
            initialValue = 60F,
            targetValue = 0F,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )
        val offset = offsetFloat.dp

        if (showScanLine) {
            if (overlayStyle == ScannerOverlayStyle.CLASSIC) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(4.dp)
                        .background(Color.Gray)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                            .background(Color.White)
                            .padding(horizontal = 16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = -offset)
                        .fillMaxWidth(0.5f)
                        .height(4.dp)
                        .background(Color.Gray)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = offset)
                        .fillMaxWidth(0.5f)
                        .height(4.dp)
                        .background(Color.Gray)
                )
            } else if (overlayStyle == ScannerOverlayStyle.ANIMATED_LINE) {
                val scanLineOffset by transition.animateFloat(
                    initialValue = -100f,
                    targetValue = 100f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ScanLine"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = scanLineOffset.dp)
                        .fillMaxWidth(0.7f)
                        .height(2.dp)
                        .background(scanLineColor)
                )
            }
        }

        Text(
            text = watermark,
            color = Color.White,
            fontSize = 20.sp,
            fontFamily = FontFamily.Cursive,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp)
                .offset(y = 25.dp)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scanningAreaWidth = size.width * 0.8f
            val scanningAreaHeight = scanningAreaWidth * 0.6f
            val left = (size.width - scanningAreaWidth) / 2f
            val top = (size.height - scanningAreaHeight) / 2f
            val right = left + scanningAreaWidth
            val bottom = top + scanningAreaHeight

            val cornerLength = scanningAreaWidth * 0.15f
            val strokeWidth = 8f

            drawLine(
                color = frameColor,
                start = Offset(left, top + cornerLength),
                end = Offset(left, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(left, top),
                end = Offset(left + cornerLength, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = frameColor,
                start = Offset(right - cornerLength, top),
                end = Offset(right, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(right, top),
                end = Offset(right, top + cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = frameColor,
                start = Offset(left, bottom - cornerLength),
                end = Offset(left, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(left, bottom),
                end = Offset(left + cornerLength, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = frameColor,
                start = Offset(right - cornerLength, bottom),
                end = Offset(right, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(right, bottom - cornerLength),
                end = Offset(right, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        }
    }