package io.github.ismoy.imagepickerkmp

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.ImagePickerUiConstants.BackgroundColor

@Suppress("LongMethod","LongParameterList")
@Composable
fun CameraCapturePreview(
    cameraManager: CameraXManager,
    preference: CapturePhotoPreference,
    onPhotoResult: (PhotoResult) -> Unit,
    context: Context,
    onError: (Exception) -> Unit,
    previewConfig: CameraPreviewConfig = CameraPreviewConfig()
) {
    val previewView = remember { PreviewView(context) }
    val coroutineScope = rememberCoroutineScope()
    val stateHolder = remember { CameraCaptureStateHolder(cameraManager, previewView,
        preference, coroutineScope) }
    var openGallery by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) MaterialTheme.colors.background else BackgroundColor
    val resolvedButtonColor = previewConfig.uiConfig.buttonColor ?: Color.Gray
    val resolvedIconColor = previewConfig.uiConfig.iconColor ?: Color.White
    val resolvedButtonSize = previewConfig.uiConfig.buttonSize ?: 56.dp
    val captureButtonSize = previewConfig.captureButtonSize
    LaunchedEffect(Unit) {
        stateHolder.startCamera(
            onError = onError,
            onCameraReady = previewConfig.cameraCallbacks.onCameraReady,
            onPermissionError = previewConfig.cameraCallbacks.onPermissionError
        )
    }

    if (openGallery) {
        GalleryPickerLauncher(
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
        FlashToggleButton(
            flashMode = stateHolder.flashMode,
            iconColor = resolvedIconColor,
            flashIcon = previewConfig.uiConfig.flashIcon,
            onToggle = { stateHolder.toggleFlash() }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 32.dp, bottom = 32.dp)
                .size(resolvedButtonSize)
                .background(resolvedButtonColor, shape = CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    openGallery = true
                    previewConfig.cameraCallbacks.onGalleryOpened?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = previewConfig.uiConfig.galleryIcon ?: Icons.Default.PhotoLibrary,
                contentDescription = "Open gallery button",
                tint = resolvedIconColor
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(captureButtonSize.coerceAtLeast(resolvedButtonSize))
                .background(Color.White, shape = CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    stateHolder.capturePhoto(onPhotoResult, onError)
                }
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 32.dp, bottom = 32.dp)
                .size(resolvedButtonSize)
                .background(resolvedButtonColor, shape = CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    stateHolder.switchCamera(onError, previewConfig.cameraCallbacks.onCameraSwitch)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = previewConfig.uiConfig.switchCameraIcon ?: Icons.Default.Cached,
                contentDescription = "Switch camera",
                tint = resolvedIconColor
            )
        }
        FlashOverlay(visible = stateHolder.showFlashOverlay)
    }
}
