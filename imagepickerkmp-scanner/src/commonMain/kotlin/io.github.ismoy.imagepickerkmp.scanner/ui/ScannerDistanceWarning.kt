package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig.General.scanner_distance_warning
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance
@Composable
internal fun ScannerDistanceWarning(
    config: ScannerCameraConfig,
    distance: CameraPositionDistance
) {
    val instructionText = when (distance) {
        CameraPositionDistance.TOO_FAR -> config.ui.tooFarText
        CameraPositionDistance.TOO_CLOSE -> config.ui.tooCloseText
        CameraPositionDistance.OPTIMAL -> config.ui.tooOptimalText
        CameraPositionDistance.UNKNOWN -> config.ui.tooFarText
    }
    val instructionColor = when (distance) {
        CameraPositionDistance.TOO_FAR -> config.ui.tooFarColor
        CameraPositionDistance.TOO_CLOSE -> config.ui.tooCloseColor
        CameraPositionDistance.OPTIMAL -> config.ui.tooOptimalColor
        CameraPositionDistance.UNKNOWN -> config.ui.tooFarColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(instructionColor.copy(alpha = 0.2f),
                    RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null,
                tint = instructionColor, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = instructionText.uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = scanner_distance_warning,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
