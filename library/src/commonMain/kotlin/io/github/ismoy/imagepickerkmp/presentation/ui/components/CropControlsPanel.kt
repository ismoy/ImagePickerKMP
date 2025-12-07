package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig

@Composable
fun CropControlsPanel(
    isCircularCrop: Boolean,
    aspectRatio: String,
    zoomLevel: Float,
    rotationAngle: Float,
    cropConfig: CropConfig,
    onToggleCropShape: (Boolean) -> Unit,
    onAspectRatioChange: (String) -> Unit,
    onZoomChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit
) {
    val defaultPadding = 16.dp
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(defaultPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = defaultPadding),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val showBothByDefault = cropConfig.circularCrop && cropConfig.squareCrop
            val showBothFallback = !cropConfig.circularCrop && !cropConfig.squareCrop

            if (showBothByDefault || cropConfig.squareCrop || showBothFallback) {
                Button(
                    onClick = { onToggleCropShape(false) },
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(32.dp)
                        .defaultMinSize(minWidth = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (!isCircularCrop) Color.White else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.White),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Crop,
                        contentDescription = "Rectangular crop",
                        tint = if (!isCircularCrop) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }


            if (showBothByDefault || cropConfig.circularCrop || showBothFallback) {
                Button(
                    onClick = { onToggleCropShape(true) },
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(32.dp)
                        .defaultMinSize(minWidth = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isCircularCrop) Color.White else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, Color.White),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Circular crop",
                        tint = if (isCircularCrop) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (showBothByDefault || cropConfig.squareCrop || showBothFallback) {
                val ratios = listOf("1:1", "4:3", "16:9", "9:16")
                ratios.forEach { ratio ->
                    Button(
                        onClick = { onAspectRatioChange(ratio) },
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .height(32.dp)
                            .defaultMinSize(minWidth = 44.dp),
                        enabled = !isCircularCrop,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (aspectRatio == ratio && !isCircularCrop) Color.White else Color.Transparent,
                            disabledBackgroundColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(1.dp, if (isCircularCrop) Color.Gray else Color.White),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ratio,
                            color = if (aspectRatio == ratio && !isCircularCrop) Color.Black
                            else if (isCircularCrop) Color.Gray
                            else Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Column {
            Text("Zoom", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
            Slider(
                value = zoomLevel,
                onValueChange = onZoomChange,
                valueRange = 0.5f..2.5f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text("Rotation", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
            Slider(
                value = rotationAngle,
                onValueChange = onRotationChange,
                valueRange = -180f..180f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White
                )
            )
        }
    }
}
