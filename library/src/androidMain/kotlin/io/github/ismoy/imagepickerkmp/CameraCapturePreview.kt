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
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.FlashAuto
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Cached
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ismoy.imagepickerkmp.CameraCaptureStateHolder

@Composable
fun CameraCapturePreview(
    cameraManager: CameraXManager,
    preference: CapturePhotoPreference,
    onPhotoResult: (PhotoResult) -> Unit,
    context: Context,
    onError: (Exception) -> Unit,
    galleryButtonSize: Dp = 56.dp,
    captureButtonSize: Dp = 72.dp,
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
    onCameraReady: (() -> Unit)? = null,
    onCameraSwitch: (() -> Unit)? = null,
    onPermissionError: ((Exception) -> Unit)? = null,
    onGalleryOpened: (() -> Unit)? = null
) {
    val previewView = remember { PreviewView(context) }
    val coroutineScope = rememberCoroutineScope()
    val stateHolder = remember { CameraCaptureStateHolder(context, cameraManager, previewView, preference, coroutineScope) }
    var openGallery by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) MaterialTheme.colors.background else Color(0xFFF5F5F5)
    val resolvedButtonColor = buttonColor ?: Color.Gray
    val resolvedIconColor = iconColor ?: Color.White
    val resolvedButtonSize = buttonSize ?: 56.dp

    LaunchedEffect(Unit) {
        stateHolder.startCamera(onError)
    }

    if (openGallery) {
        GalleryPickerLauncher(
            context = context,
            onPhotosSelected = { results ->
                openGallery = false
                val result = results.firstOrNull()
                if (result != null) {
                    onPhotoResult(
                        PhotoResult(
                            uri = result.uri,
                            width = result.width,
                            height = result.height
                        )
                    )
                }
            },
            onError = {
                openGallery = false
                onError(it)
            },
            allowMultiple = false,
            mimeTypes = listOf("image/*")
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AnimatedVisibility(
            visible = !stateHolder.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }
        AnimatedVisibility(
            visible = stateHolder.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
                .background(Color(0xAA444444), shape = RoundedCornerShape(50))
                .padding(horizontal = 30.dp, vertical = 8.dp)
                .clickable { stateHolder.toggleFlash() },
            contentAlignment = Alignment.Center
        ) {
            val icon = flashIcon ?: when (stateHolder.flashMode) {
                CameraController.FlashMode.AUTO -> Icons.Outlined.FlashAuto
                CameraController.FlashMode.ON -> Icons.Default.FlashOn
                CameraController.FlashMode.OFF -> Icons.Default.FlashOff
            }
            Icon(
                imageVector = icon,
                contentDescription = "Flash mode",
                tint = resolvedIconColor
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
                .size(resolvedButtonSize)
                .background(resolvedButtonColor, shape = CircleShape)
                .clickable { openGallery = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = galleryIcon ?: Icons.Default.PhotoLibrary,
                contentDescription = "Open gallery button",
                tint = resolvedIconColor
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(captureButtonSize.coerceAtLeast(resolvedButtonSize))
                .background(Color.White.copy(alpha = 0.9f), shape = CircleShape)
                .clickable {
                    stateHolder.capturePhoto(onPhotoResult, onError)
                },
            contentAlignment = Alignment.Center
        ) {}

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 32.dp)
                .size(resolvedButtonSize)
                .background(resolvedButtonColor, shape = CircleShape)
                .clickable {
                    stateHolder.switchCamera(onError)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = switchCameraIcon ?: Icons.Default.Cached,
                contentDescription = "Switch camera",
                tint = resolvedIconColor
            )
        }

        AnimatedVisibility(
            visible = stateHolder.showFlashOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f))
            )
        }
    }
}