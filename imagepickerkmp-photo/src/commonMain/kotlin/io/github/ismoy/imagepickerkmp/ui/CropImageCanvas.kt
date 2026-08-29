package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil3.compose.LocalPlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import io.github.ismoy.imagepickerkmp.crop.CropHandle
import io.github.ismoy.imagepickerkmp.crop.CropUtils.detectHandle
import io.github.ismoy.imagepickerkmp.crop.drawCropHandles
import io.github.ismoy.imagepickerkmp.crop.resizeCropRect
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun CropImageCanvas(
    photoResult: PhotoResult,
    imageSize: Size,
    zoomLevel: Float,
    rotationAngle: Float,
    cropRect: Rect,
    canvasSize: Size,
    isDragging: Boolean,
    activeHandle: CropHandle?,
    dragStartOffset: Offset,
    isCircularCrop: Boolean,
    constrainCropToImageBounds: Boolean,
    onImageSizeChanged: (Size) -> Unit,
    onCropRectChanged: (Rect) -> Unit,
    onCanvasSizeChanged: (Size) -> Unit,
    onDragStateChanged: (Boolean, CropHandle?, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageBounds = displayedImageBounds(imageSize, canvasSize, zoomLevel)
    val initialImageBounds = displayedImageBounds(imageSize, canvasSize, 1f)

    LaunchedEffect(cropRect, imageBounds, constrainCropToImageBounds) {
        if (constrainCropToImageBounds && cropRect != Rect.Zero && imageBounds != null) {
            val constrainedCropRect = cropRect.constrainTo(imageBounds)
            if (constrainedCropRect != cropRect) onCropRectChanged(constrainedCropRect)
        }
    }

    Column(modifier = modifier) {
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
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(photoResult.uri)
                    .size(1000)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .build(),
                contentDescription = "Image to crop",
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
                    .pointerInput(
                        imageBounds,
                        constrainCropToImageBounds,
                        isCircularCrop,
                        localCanvasSize
                    ) {
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
                                if (!localIsDragging) return@detectDragGestures

                                val updatedCropRect = if (localActiveHandle != null) {
                                    val resizedRect = resizeCropRect(
                                        localCropRect,
                                        localActiveHandle!!,
                                        dragAmount,
                                        localCanvasSize
                                    )
                                    if (isCircularCrop) {
                                        circularCropRect(localCropRect, resizedRect, localActiveHandle!!)
                                    } else {
                                        resizedRect
                                    }
                                } else {
                                    val movedCropRect = Rect(
                                        offset = Offset(
                                            x = localCropRect.left + dragAmount.x,
                                            y = localCropRect.top + dragAmount.y
                                        ),
                                        size = Size(localCropRect.width, localCropRect.height)
                                    )
                                    if (constrainCropToImageBounds && imageBounds != null) {
                                        movedCropRect
                                    } else {
                                        movedCropRect.constrainTo(
                                            Rect(0f, 0f, localCanvasSize.width, localCanvasSize.height)
                                        )
                                    }
                                }

                                localCropRect = if (constrainCropToImageBounds && imageBounds != null) {
                                    updatedCropRect.constrainTo(imageBounds)
                                } else {
                                    updatedCropRect
                                }
                                onCropRectChanged(localCropRect)
                            }
                        )
                    }
            ) {
                localCanvasSize = size
                onCanvasSizeChanged(size)

                if (localCropRect == Rect.Zero &&
                    (!constrainCropToImageBounds ||
                        (imageBounds != null && initialImageBounds != null))
                ) {
                    localCropRect = if (constrainCropToImageBounds && imageBounds != null &&
                        initialImageBounds != null
                    ) {
                        initialCropRectForImage(initialImageBounds, isCircularCrop)
                            .constrainTo(imageBounds)
                    } else {
                        defaultCropRect(size, isCircularCrop)
                    }
                    onCropRectChanged(localCropRect)
                }

                drawCropOverlay(localCropRect, isCircularCrop, size)
            }
        }
    }
}

private fun displayedImageBounds(imageSize: Size, canvasSize: Size, zoomLevel: Float): Rect? {
    if (imageSize.width <= 0f || imageSize.height <= 0f ||
        canvasSize.width <= 0f || canvasSize.height <= 0f
    ) {
        return null
    }

    val imageAspectRatio = imageSize.width / imageSize.height
    val canvasAspectRatio = canvasSize.width / canvasSize.height
    val baseSize = if (imageAspectRatio > canvasAspectRatio) {
        Size(canvasSize.width, canvasSize.width / imageAspectRatio)
    } else {
        Size(canvasSize.height * imageAspectRatio, canvasSize.height)
    }
    val scaledSize = Size(baseSize.width * zoomLevel, baseSize.height * zoomLevel)
    val offset = Offset(
        x = (canvasSize.width - scaledSize.width) / 2f,
        y = (canvasSize.height - scaledSize.height) / 2f
    )
    return Rect(offset, scaledSize)
}

private fun initialCropRectForImage(imageBounds: Rect, isCircularCrop: Boolean): Rect {
    if (!isCircularCrop) return imageBounds

    val side = min(imageBounds.width, imageBounds.height)
    return Rect(
        left = imageBounds.center.x - side / 2f,
        top = imageBounds.center.y - side / 2f,
        right = imageBounds.center.x + side / 2f,
        bottom = imageBounds.center.y + side / 2f
    )
}

private fun defaultCropRect(canvasSize: Size, isCircularCrop: Boolean): Rect {
    val margin = 40f
    val availableWidth = canvasSize.width - margin * 2
    val availableHeight = canvasSize.height - margin * 2
    val rectSize = if (isCircularCrop) {
        val side = min(availableWidth, availableHeight) * 0.7f
        Size(side, side)
    } else {
        Size(availableWidth * 0.8f, availableHeight * 0.6f)
    }
    return Rect(
        left = canvasSize.width / 2f - rectSize.width / 2f,
        top = canvasSize.height / 2f - rectSize.height / 2f,
        right = canvasSize.width / 2f + rectSize.width / 2f,
        bottom = canvasSize.height / 2f + rectSize.height / 2f
    )
}

private fun circularCropRect(
    currentCropRect: Rect,
    resizedCropRect: Rect,
    activeHandle: CropHandle
): Rect {
    val centerX = currentCropRect.center.x
    val centerY = currentCropRect.center.y
    val deltaFromCenter = when (activeHandle) {
        CropHandle.TOP_LEFT, CropHandle.TOP_RIGHT,
        CropHandle.BOTTOM_LEFT, CropHandle.BOTTOM_RIGHT -> max(
            abs(resizedCropRect.left - resizedCropRect.center.x),
            abs(resizedCropRect.top - resizedCropRect.center.y)
        )
        CropHandle.TOP_CENTER, CropHandle.BOTTOM_CENTER -> resizedCropRect.height / 2f
        CropHandle.LEFT_CENTER, CropHandle.RIGHT_CENTER -> resizedCropRect.width / 2f
    }
    return Rect(
        left = centerX - deltaFromCenter,
        top = centerY - deltaFromCenter,
        right = centerX + deltaFromCenter,
        bottom = centerY + deltaFromCenter
    )
}

private fun Rect.constrainTo(bounds: Rect): Rect {
    if (width <= 0f || height <= 0f || bounds.width <= 0f || bounds.height <= 0f) {
        return this
    }

    val scale = min(1f, min(bounds.width / width, bounds.height / height))
    val constrainedWidth = min(width * scale, bounds.width)
    val constrainedHeight = min(height * scale, bounds.height)
    val maxLeft = max(bounds.left, bounds.right - constrainedWidth)
    val maxTop = max(bounds.top, bounds.bottom - constrainedHeight)
    val constrainedLeft = left.coerceIn(bounds.left, maxLeft)
    val constrainedTop = top.coerceIn(bounds.top, maxTop)
    return Rect(
        left = constrainedLeft,
        top = constrainedTop,
        right = constrainedLeft + constrainedWidth,
        bottom = constrainedTop + constrainedHeight
    )
}

private fun DrawScope.drawCropOverlay(
    cropRect: Rect,
    isCircularCrop: Boolean,
    canvasSize: Size
) {
    val overlayPath = Path().apply {
        addRect(Rect(0f, 0f, canvasSize.width, canvasSize.height))
        if (isCircularCrop) {
            val radius = min(cropRect.width, cropRect.height) / 2f
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
        val radius = min(cropRect.width, cropRect.height) / 2f
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

        val thirdWidth = cropRect.width / 3f
        val thirdHeight = cropRect.height / 3f

        for (index in 1..2) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(cropRect.left + thirdWidth * index, cropRect.top),
                end = Offset(cropRect.left + thirdWidth * index, cropRect.bottom),
                strokeWidth = 1f
            )
        }

        for (index in 1..2) {
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(cropRect.left, cropRect.top + thirdHeight * index),
                end = Offset(cropRect.right, cropRect.top + thirdHeight * index),
                strokeWidth = 1f
            )
        }

        drawCropHandles(cropRect)
    }
}
