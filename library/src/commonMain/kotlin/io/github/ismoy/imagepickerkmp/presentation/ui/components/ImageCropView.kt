package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.applyCrop
import io.github.ismoy.imagepickerkmp.domain.utils.applyCropAspectRatio

@Composable
fun ImageCropView(
    photoResult: PhotoResult,
    cropConfig: CropConfig,
    onAccept: (PhotoResult) -> Unit,
    onCancel: () -> Unit
) {
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var activeHandle by remember { mutableStateOf<CropHandle?>(null) }
    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var aspectRatio by remember { mutableStateOf("Free") }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var isCircularCrop by remember { mutableStateOf(cropConfig.circularCrop) }
    var shouldApplyCrop by remember { mutableStateOf(false) }
    
    if (shouldApplyCrop) {
        applyCrop(
            photoResult = photoResult,
            cropRect = cropRect,
            canvasSize = Size(canvasSize.width, canvasSize.height),
            isCircularCrop = isCircularCrop,
            onComplete = onAccept
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        CropHeaderControls(
            onCancel = onCancel,
            applyCrop = {
                shouldApplyCrop = true
            }
        )
        Box(
            modifier = Modifier.weight(1f)
        ) {
            CropImageCanvas(
                photoResult = photoResult,
                zoomLevel = zoomLevel,
                rotationAngle = rotationAngle,
                cropRect = cropRect,
                canvasSize = canvasSize,
                isDragging = isDragging,
                activeHandle = activeHandle,
                dragStartOffset = dragStartOffset,
                isCircularCrop = isCircularCrop,
                onImageSizeChanged = { newSize -> imageSize = newSize },
                onCropRectChanged = { newRect -> cropRect = newRect },
                onCanvasSizeChanged = { newSize -> canvasSize = newSize },
                onDragStateChanged = { dragging, handle, startOffset ->
                    isDragging = dragging
                    activeHandle = handle
                    dragStartOffset = startOffset
                }
            )
        }
        CropControlsPanel(
            isCircularCrop = isCircularCrop,
            aspectRatio = aspectRatio,
            zoomLevel = zoomLevel,
            rotationAngle = rotationAngle,
            cropConfig = cropConfig,
            onToggleCropShape = { isCircular ->
                isCircularCrop = isCircular
                if (isCircular) {
                    aspectRatio = "1:1"
                    cropRect = applyCropAspectRatio(cropRect, "1:1", canvasSize)
                }
            },
            onAspectRatioChange = { ratio ->
                if (!isCircularCrop) {
                    aspectRatio = ratio
                    cropRect = applyCropAspectRatio(cropRect, ratio, canvasSize)
                }
            },
            onZoomChange = { zoomLevel = it },
            onRotationChange = { rotationAngle = it }
        )
    }
}

