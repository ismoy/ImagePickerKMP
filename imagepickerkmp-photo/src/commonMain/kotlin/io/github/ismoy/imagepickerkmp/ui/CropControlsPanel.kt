package io.github.ismoy.imagepickerkmp.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ismoy.imagepickerkmp.I18nKonfig
import io.github.ismoy.imagepickerkmp.config.CropConfig

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
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(defaultPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = defaultPadding),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val showBothByDefault = cropConfig.circularCrop && cropConfig.squareCrop

            if (showBothByDefault || cropConfig.squareCrop) {
                Button(
                    onClick = { onToggleCropShape(false) },
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(32.dp)
                        .defaultMinSize(minWidth = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCircularCrop) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Crop,
                        contentDescription = I18nKonfig.General.image_crop_view_rectangular_description,
                        tint = if (!isCircularCrop) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }


            if (showBothByDefault || cropConfig.circularCrop) {
                Button(
                    onClick = { onToggleCropShape(true) },
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .height(32.dp)
                        .defaultMinSize(minWidth = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCircularCrop) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = I18nKonfig.General.image_crop_view_circular_description,
                        tint = if (isCircularCrop) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (showBothByDefault || cropConfig.squareCrop) {
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
                            containerColor = if (aspectRatio == ratio && !isCircularCrop) MaterialTheme.colorScheme.primary else Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isCircularCrop) MaterialTheme.colorScheme.outline.copy(alpha = 0.38f) else MaterialTheme.colorScheme.outline
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ratio,
                            color = if (aspectRatio == ratio && !isCircularCrop) MaterialTheme.colorScheme.onPrimary
                            else if (isCircularCrop) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Column {
            Text(
                text = I18nKonfig.General.image_crop_view_zoom_label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Slider(
                value = zoomLevel,
                onValueChange = onZoomChange,
                valueRange = 0.5f..3.0f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = I18nKonfig.General.image_crop_view_rotation_label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Slider(
                value = rotationAngle,
                onValueChange = onRotationChange,
                valueRange = -180f..180f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
