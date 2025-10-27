package io.github.ismoy.imagepickerkmp.presentation.ui.components

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
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.domain.config.CameraPreviewConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.BackgroundColor
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import android.view.ViewGroup.LayoutParams
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import io.github.ismoy.imagepickerkmp.presentation.ui.utils.rememberCameraManager

private const val CAMERA_INITIALIZATION_DELAY = 0L

@Suppress("LongMethod","LongParameterList")
@Composable
fun CameraCapturePreview(
    preference: CapturePhotoPreference,
    onPhotoResult: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    previewConfig: CameraPreviewConfig = CameraPreviewConfig(),
    compressionLevel: CompressionLevel? = null
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
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
    var openGallery by remember { mutableStateOf(false) }
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

    if (openGallery) {
        GalleryPickerLauncher(
            onPhotosSelected = { results: List<GalleryPhotoResult> ->
                openGallery = false
                val result = results.firstOrNull()
                if (result != null) {
                    onPhotoResult(
                        PhotoResult(
                            uri = result.uri,
                            width = result.width,
                            height = result.height,
                            fileName = result.fileName,
                            fileSize = result.fileSize
                        )
                    )
                }
            },
            onError = { error: Exception ->
                openGallery = false
                onError(error)
            },
            onDismiss = { openGallery = false },
            allowMultiple = false,
            mimeTypes = listOf(MimeType.IMAGE_ALL),
            selectionLimit = 1L,
            cameraCaptureConfig = null,
            enableCrop = false
        )
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
                                .setDuration(100)
                                .setStartDelay(50)
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
            flashMode = stateHolder?.flashMode ?: CameraController.FlashMode.AUTO,
            iconColor = resolvedIconColor,
            flashIcon = previewConfig.uiConfig.flashIcon,
            onToggle = { stateHolder?.toggleFlash() }
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
                    stateHolder?.capturePhoto(onPhotoResult, onError, compressionLevel)
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
