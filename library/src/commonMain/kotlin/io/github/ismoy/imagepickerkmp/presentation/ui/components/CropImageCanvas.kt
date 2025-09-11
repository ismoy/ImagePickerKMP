package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.models.CropHandle
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.utils.drawCropHandles
import io.github.ismoy.imagepickerkmp.domain.utils.resizeCropRect
import io.github.ismoy.imagepickerkmp.presentation.ui.components.helper.utility.CropUtils.detectHandle
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
 fun CropImageCanvas(
    photoResult: PhotoResult,
    zoomLevel: Float,
    rotationAngle: Float,
    cropRect: Rect,
    canvasSize: Size,
    isDragging: Boolean,
    activeHandle: CropHandle?,
    dragStartOffset: Offset,
    isCircularCrop: Boolean,
    onImageSizeChanged: (Size) -> Unit,
    onCropRectChanged: (Rect) -> Unit,
    onCanvasSizeChanged: (Size) -> Unit,
    onDragStateChanged: (Boolean, CropHandle?, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        var localCropRect by remember(cropRect) { mutableStateOf(cropRect) }
        var localCanvasSize by remember(canvasSize) { mutableStateOf(canvasSize) }
        var localIsDragging by remember(isDragging) { mutableStateOf(isDragging) }
        var localActiveHandle by remember(activeHandle) { mutableStateOf(activeHandle) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
        ) {
            AsyncImage(
                model = photoResult.uri,
                contentDescription = "Imagen a recortar",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = zoomLevel,
                        scaleY = zoomLevel,
                        rotationZ = rotationAngle
                    ),
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    val painter = state.painter
                    onImageSizeChanged(
                        Size(
                            painter.intrinsicSize.width,
                            painter.intrinsicSize.height
                        )
                    )
                }
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val handle = detectHandle(offset, localCropRect)
                                if (handle != null) {
                                    localActiveHandle = handle
                                    localIsDragging = true
                                    onDragStateChanged(true, handle, offset)
                                } else if (localCropRect.contains(offset)) {
                                    localActiveHandle = null
                                    localIsDragging = true
                                    onDragStateChanged(true, null, offset)
                                }
                            },
                            onDragEnd = {
                                localIsDragging = false
                                localActiveHandle = null
                                onDragStateChanged(false, null, Offset.Zero)
                            },
                            onDrag = { _, dragAmount ->
                                if (localIsDragging) {
                                    if (localActiveHandle != null) {
                                        val newRect = resizeCropRect(
                                            localCropRect,
                                            localActiveHandle!!,
                                            dragAmount,
                                            localCanvasSize
                                        )

                                        if (isCircularCrop) {
                                            val centerX = localCropRect.center.x
                                            val centerY = localCropRect.center.y
                                            val deltaFromCenter = when (localActiveHandle!!) {
                                                CropHandle.TOP_LEFT, CropHandle.TOP_RIGHT,
                                                CropHandle.BOTTOM_LEFT, CropHandle.BOTTOM_RIGHT -> {
                                                    val newCenterX = newRect.center.x
                                                    val newCenterY = newRect.center.y
                                                    max(
                                                        abs(newRect.left - newCenterX),
                                                        abs(newRect.top - newCenterY)
                                                    )
                                                }
                                                CropHandle.TOP_CENTER, CropHandle.BOTTOM_CENTER -> {
                                                    newRect.height / 2
                                                }
                                                CropHandle.LEFT_CENTER, CropHandle.RIGHT_CENTER -> {
                                                    newRect.width / 2
                                                }
                                            }

                                            localCropRect = Rect(
                                                left = centerX - deltaFromCenter,
                                                top = centerY - deltaFromCenter,
                                                right = centerX + deltaFromCenter,
                                                bottom = centerY + deltaFromCenter
                                            )
                                        } else {
                                            localCropRect = newRect
                                        }
                                        onCropRectChanged(localCropRect)
                                    } else {
                                        val newLeft = (localCropRect.left + dragAmount.x)
                                            .coerceAtLeast(0f)
                                            .coerceAtMost(localCanvasSize.width - localCropRect.width)
                                        val newTop = (localCropRect.top + dragAmount.y)
                                            .coerceAtLeast(0f)
                                            .coerceAtMost(localCanvasSize.height - localCropRect.height)

                                        localCropRect = Rect(
                                            offset = Offset(newLeft, newTop),
                                            size = Size(localCropRect.width, localCropRect.height)
                                        )
                                        onCropRectChanged(localCropRect)
                                    }
                                }
                            }
                        )
                    }
            ) {
                localCanvasSize = size
                onCanvasSizeChanged(size)

                if (localCropRect == Rect.Zero) {
                    val margin = 40f
                    val availableWidth = size.width - margin * 2
                    val availableHeight = size.height - margin * 2
                    val rectWidth = availableWidth * 0.8f
                    val rectHeight = availableHeight * 0.6f
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    localCropRect = Rect(
                        left = centerX - rectWidth / 2,
                        top = centerY - rectHeight / 2,
                        right = centerX + rectWidth / 2,
                        bottom = centerY + rectHeight / 2
                    )
                    onCropRectChanged(localCropRect)
                }

                drawCropOverlay(localCropRect, isCircularCrop, size)
            }
        }
    }
}
private fun DrawScope.drawCropOverlay(
    cropRect: Rect,
    isCircularCrop: Boolean,
    canvasSize: Size
) {
    val overlayPath = Path().apply {
        addRect(Rect(0f, 0f, canvasSize.width, canvasSize.height))
        if (isCircularCrop) {
            val radius = min(cropRect.width, cropRect.height) / 2
            addOval(
                Rect(
                    cropRect.center.x - radius,
                    cropRect.center.y - radius,
                    cropRect.center.x + radius,
                    cropRect.center.y + radius
                )
            )
        } else {
            addRect(cropRect)
        }
    }

    clipPath(overlayPath, clipOp = ClipOp.Difference) {
        drawRect(Color.Black.copy(alpha = 0.5f))
    }

    if (isCircularCrop) {
        val radius = min(cropRect.width, cropRect.height) / 2
        drawCircle(
            color = Color.White,
            radius = radius,
            center = cropRect.center,
            style = Stroke(width = 2f)
        )
        drawCropHandles(cropRect)
    } else {
        drawRect(
            color = Color.White,
            topLeft = cropRect.topLeft,
            size = Size(cropRect.width, cropRect.height),
            style = Stroke(width = 2f)
        )

        val thirdWidth = cropRect.width / 3
        val thirdHeight = cropRect.height / 3

        for (i in 1..2) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(cropRect.left + thirdWidth * i, cropRect.top),
                end = Offset(cropRect.left + thirdWidth * i, cropRect.bottom),
                strokeWidth = 1f
            )
        }

        for (i in 1..2) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(cropRect.left, cropRect.top + thirdHeight * i),
                end = Offset(cropRect.right, cropRect.top + thirdHeight * i),
                strokeWidth = 1f
            )
        }

        drawCropHandles(cropRect)
    }
}