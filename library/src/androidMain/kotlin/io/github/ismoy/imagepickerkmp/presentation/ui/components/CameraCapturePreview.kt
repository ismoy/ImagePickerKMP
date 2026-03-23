package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.view.ViewGroup.LayoutParams
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.data.models.FlashMode
import io.github.ismoy.imagepickerkmp.domain.config.CameraPreviewConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.BackgroundColor
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.DELAY_TO_TAKE_PHOTO
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.extensions.activity
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.playShutterSound
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.rememberCameraManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

private const val CAMERA_INITIALIZATION_DELAY = 0L

@Suppress("LongMethod","LongParameterList")
@Composable
fun CameraCapturePreview(
    preference: CapturePhotoPreference,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    previewConfig: CameraPreviewConfig = CameraPreviewConfig(),
    compressionLevel: CompressionLevel? = null,
    includeExif: Boolean = false,
    redactGpsData: Boolean = true
) {
    val context = LocalContext.current
    val activity = context.activity as? ComponentActivity
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    val cameraManager = rememberCameraManager(context, activity ?: return)
    val stateHolder: CameraCaptureStateHolder? = previewView?.let { view ->
        cameraManager?.let { manager ->
            remember(view, preference, manager) {
                CameraCaptureStateHolder(
                    cameraManager = manager,
                    previewView = view,
                    preference = preference,
                    coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                )
            }
        }
    }

    DisposableEffect(stateHolder) {
        onDispose { stateHolder?.cancel() }
    }

    VolumeButtonCapture {
        playShutterSound()
        stateHolder?.capturePhoto(onPhotoResult, onError, compressionLevel, includeExif, redactGpsData)
    }
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) MaterialTheme.colors.background else BackgroundColor
    val resolvedButtonColor = previewConfig.uiConfig.buttonColor ?: Color.Gray
    val resolvedIconColor = previewConfig.uiConfig.iconColor ?: Color.White
    val resolvedButtonSize = previewConfig.uiConfig.buttonSize ?: 56.dp
    val captureButtonSize = previewConfig.captureButtonSize

    LaunchedEffect(stateHolder) {
        stateHolder?.let { holder ->
            delay(CAMERA_INITIALIZATION_DELAY)
            holder.startCamera(
                onError = onError,
                onCameraReady = previewConfig.cameraCallbacks.onCameraReady,
                onPermissionError = previewConfig.cameraCallbacks.onPermissionError
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                        layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                        )
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        alpha = 0f
                        post {
                            animate()
                                .alpha(1f)
                                .setDuration(DELAY_TO_TAKE_PHOTO)
                                .setStartDelay(DELAY_TO_TAKE_PHOTO)
                                .start()
                        }
                        previewView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    if (view.display != null) {
                        view.requestLayout()
                    }
                }
            )

        FlashToggleButton(
            flashMode = stateHolder?.flashMode ?: FlashMode.AUTO,
            iconColor = resolvedIconColor,
            flashIcon = previewConfig.uiConfig.flashIcon,
            onToggle = { stateHolder?.toggleFlash() }
        )
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
                    playShutterSound()
                    stateHolder?.capturePhoto(onPhotoResult, onError, compressionLevel, includeExif, redactGpsData)
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
                    stateHolder?.switchCamera(onError, previewConfig.cameraCallbacks.onCameraSwitch)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = previewConfig.uiConfig.switchCameraIcon ?: Icons.Default.Cached,
                contentDescription = "Switch camera",
                tint = resolvedIconColor
            )
        }
        FlashOverlay(visible = stateHolder?.showFlashOverlay ?: false)
    }
}
