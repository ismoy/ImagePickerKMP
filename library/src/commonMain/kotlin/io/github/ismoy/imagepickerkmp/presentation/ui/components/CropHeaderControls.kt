package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import imagepickerkmp.library.generated.resources.Res
import imagepickerkmp.library.generated.resources.image_crop_view_apply_description
import imagepickerkmp.library.generated.resources.image_crop_view_cancel_description
import imagepickerkmp.library.generated.resources.image_crop_view_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun CropHeaderControls(
    onCancel: () -> Unit,
    applyCrop: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A))
            .padding(8.dp)
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.image_crop_view_cancel_description),
                tint = Color.White
            )
        }

        Text(
            text = stringResource(Res.string.image_crop_view_title),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = applyCrop,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.image_crop_view_apply_description),
                tint = Color.White
            )
        }
    }
}
