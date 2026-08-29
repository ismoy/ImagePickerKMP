package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.crop.ApplyCrop
import io.github.ismoy.imagepickerkmp.crop.CropHandle
import io.github.ismoy.imagepickerkmp.crop.applyCropAspectRatio
import io.github.ismoy.imagepickerkmp.picker.PhotoResult

private const val MIN_SUPPORTED_ZOOM = 0.1f
private const val MAX_SUPPORTED_ZOOM = 3f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImageCropView(
    photoResult: PhotoResult,
    cropConfig: CropConfig,
    onAccept: (PhotoResult) -> Unit,
    onCancel: () -> Unit,
    onSkip: (() -> Unit)? = null
) {
    val minimumZoom = cropConfig.minZoom.coerceIn(MIN_SUPPORTED_ZOOM, MAX_SUPPORTED_ZOOM)
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var activeHandle by remember { mutableStateOf<CropHandle?>(null) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var aspectRatio by remember { mutableStateOf("Free") }
    var zoomLevel by remember(cropConfig.initialZoom, minimumZoom) {
        mutableFloatStateOf(cropConfig.initialZoom.coerceIn(minimumZoom, MAX_SUPPORTED_ZOOM))
    }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isCircularCrop by remember { mutableStateOf(cropConfig.circularCrop) }
    var shouldApplyCrop by remember { mutableStateOf(false) }
    val loadingInteractionSource = remember { MutableInteractionSource() }

    if (shouldApplyCrop) {
        ApplyCrop(
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = canvasSize,
            isCircularCrop = isCircularCrop,
            zoomLevel = zoomLevel,
            rotationAngle = rotationAngle,
            onComplete = onAccept
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CropHeaderControls(
                onCancel = { if (!shouldApplyCrop) onCancel() },
                onSkip = if (cropConfig.allowSkip && !shouldApplyCrop) onSkip else null,
                applyCrop = { if (!shouldApplyCrop) shouldApplyCrop = true }
            )
            Box(modifier = Modifier.weight(1f)) {
                CropImageCanvas(
                    photoResult = photoResult,
                    imageSize = imageSize,
                    zoomLevel = zoomLevel,
                    rotationAngle = rotationAngle,
                    cropRect = cropRect,
                    canvasSize = canvasSize,
                    isDragging = isDragging,
                    activeHandle = activeHandle,
                    dragStartOffset = dragStartOffset,
                    isCircularCrop = isCircularCrop,
                    constrainCropToImageBounds = cropConfig.constrainCropToImageBounds,
                    onImageSizeChanged = { newSize -> imageSize = newSize },
                    onCropRectChanged = { newRect ->
                        if (!shouldApplyCrop) cropRect = newRect
                    },
                    onCanvasSizeChanged = { newSize -> canvasSize = newSize },
                    onDragStateChanged = { dragging, handle, startOffset ->
                        if (!shouldApplyCrop) {
                            isDragging = dragging
                            activeHandle = handle
                            dragStartOffset = startOffset
                        }
                    }
                )
            }
            CropControlsPanel(
                isCircularCrop = isCircularCrop,
                aspectRatio = aspectRatio,
                zoomLevel = zoomLevel,
                minZoom = minimumZoom,
                rotationAngle = rotationAngle,
                cropConfig = cropConfig,
                onToggleCropShape = { isCircular ->
                    if (!shouldApplyCrop) {
                        isCircularCrop = isCircular
                        if (isCircular) {
                            aspectRatio = "1:1"
                            cropRect = applyCropAspectRatio(cropRect, "1:1", canvasSize)
                        }
                    }
                },
                onAspectRatioChange = { ratio ->
                    if (!shouldApplyCrop && !isCircularCrop) {
                        aspectRatio = ratio
                        cropRect = applyCropAspectRatio(cropRect, ratio, canvasSize)
                    }
                },
                onZoomChange = { if (!shouldApplyCrop) zoomLevel = it },
                onRotationChange = { if (!shouldApplyCrop) rotationAngle = it }
            )
        }

        if (shouldApplyCrop) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = loadingInteractionSource,
                        indication = null,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
        }
    }
}
