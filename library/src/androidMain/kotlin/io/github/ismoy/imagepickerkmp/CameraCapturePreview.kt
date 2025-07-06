package io.github.ismoy.imagepickerkmp

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult

@Composable
fun CameraCapturePreview(
    cameraManager: CameraXManager,
    preference: CapturePhotoPreference,
    onPhotoResult: (PhotoResult) -> Unit,
    context: Context,
    onError: (Exception) -> Unit
){
    val previewView = remember { PreviewView(context) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        try {
            cameraManager.startCamera(previewView, preference)
        } catch (e: Exception) {
            onError(e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp)
                .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                .clickable(interactionSource = interactionSource, indication = null) {
                    cameraManager.takePicture(onPhotoResult, onError)
                }
        )
    }
}