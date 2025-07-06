package io.github.ismoy.imagepickerkmp

import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.Constant.BTN_ACCEPT
import io.github.ismoy.imagepickerkmp.Constant.BTN_RETRY

@Composable
fun BoxScope.ImageConfirmationViewWithCustomButtons(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null
) {
    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageURI(result.uri.toUri())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    if (customConfirmationView != null) {
        customConfirmationView(result, onConfirm, onRetry)
    } else {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(
                onClick = onRetry,
                border = BorderStroke(1.dp, Color.Red),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text(BTN_RETRY, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { onConfirm(result) },
                border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4CAF50))
            ) {
                Text(BTN_ACCEPT, fontWeight = FontWeight.Bold)
            }
        }
    }
}