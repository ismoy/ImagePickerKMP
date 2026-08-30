package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import kotlin.math.roundToInt

@Composable
internal fun ScannerAROverlay(
    barcodes: List<BarcodeData>,
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    strokeWidth: Float = 6f,
    customBarcodeContent: (@Composable (BarcodeData) -> Unit)? = null,
    onBarcodeTap: ((BarcodeData) -> Unit)? = null
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                .pointerInput(barcodes, canvasSize) {
                    detectTapGestures { offset ->
                        if (canvasSize == Size.Zero || onBarcodeTap == null) return@detectTapGestures

                        val canvasWidth = canvasSize.width
                        val canvasHeight = canvasSize.height

                        for (barcode in barcodes) {
                            val rect = barcode.boundingBox ?: continue
                            if (rect.sourceWidth <= 0 || rect.sourceHeight <= 0) continue

                            val sourceRatio = rect.sourceWidth / rect.sourceHeight
                            val canvasRatio = canvasWidth / canvasHeight

                            var scale = 1f
                            var offsetX = 0f
                            var offsetY = 0f

                            if (sourceRatio > canvasRatio) {
                                scale = canvasHeight / rect.sourceHeight
                                val scaledWidth = rect.sourceWidth * scale
                                offsetX = -(scaledWidth - canvasWidth) / 2f
                            } else {
                                scale = canvasWidth / rect.sourceWidth
                                val scaledHeight = rect.sourceHeight * scale
                                offsetY = -(scaledHeight - canvasHeight) / 2f
                            }

                            val scaledLeft = (rect.left * scale) + offsetX
                            val scaledTop = (rect.top * scale) + offsetY
                            val scaledRight = (rect.right * scale) + offsetX
                            val scaledBottom = (rect.bottom * scale) + offsetY
                            val padding = 20f
                            if (offset.x >= scaledLeft - padding && offset.x <= scaledRight + padding &&
                                offset.y >= scaledTop - padding && offset.y <= scaledBottom + padding) {
                                onBarcodeTap(barcode)
                                break
                            }
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            for (barcode in barcodes) {
                val rect = barcode.boundingBox ?: continue
                if (rect.sourceWidth <= 0 || rect.sourceHeight <= 0) continue

                val sourceRatio = rect.sourceWidth / rect.sourceHeight
                val canvasRatio = canvasWidth / canvasHeight

                var scale = 1f
                var offsetX = 0f
                var offsetY = 0f

                if (sourceRatio > canvasRatio) {
                    scale = canvasHeight / rect.sourceHeight
                    val scaledWidth = rect.sourceWidth * scale
                    offsetX = -(scaledWidth - canvasWidth) / 2f
                } else {
                    scale = canvasWidth / rect.sourceWidth
                    val scaledHeight = rect.sourceHeight * scale
                    offsetY = -(scaledHeight - canvasHeight) / 2f
                }

                val scaledLeft = (rect.left * scale) + offsetX
                val scaledTop = (rect.top * scale) + offsetY
                val scaledWidth = rect.width * scale
                val scaledHeight = rect.height * scale

                if (!barcode.cornerPoints.isNullOrEmpty() && barcode.cornerPoints.size == 4) {
                    val path = androidx.compose.ui.graphics.Path()
                    barcode.cornerPoints.forEachIndexed { index, point ->
                        val scaledPx = (point.x * scale) + offsetX
                        val scaledPy = (point.y * scale) + offsetY
                        if (index == 0) {
                            path.moveTo(scaledPx, scaledPy)
                        } else {
                            path.lineTo(scaledPx, scaledPy)
                        }
                    }
                    path.close()
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(scaledLeft, scaledTop),
                        size = Size(scaledWidth, scaledHeight),
                        cornerRadius = CornerRadius(12f, 12f),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
        }
        if (customBarcodeContent != null && canvasSize != Size.Zero) {
            val canvasWidth = canvasSize.width
            val canvasHeight = canvasSize.height
            val sortedBarcodes = barcodes.sortedBy { it.boundingBox?.top ?: 0f }

            Layout(
                content = {
                    for (barcode in sortedBarcodes) {
                        Box { customBarcodeContent(barcode) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { measurables, constraints ->
                val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

                layout(constraints.maxWidth, constraints.maxHeight) {
                    data class CardAnchor(
                        val barcode: BarcodeData,
                        val anchorCx: Float,
                        val anchorCy: Float,
                        val barcodeTop: Float,
                        val barcodeBottom: Float,
                    )

                    val anchors = sortedBarcodes.map { barcode ->
                        val rect = barcode.boundingBox
                        if (rect == null || rect.sourceWidth <= 0 || rect.sourceHeight <= 0) {
                            CardAnchor(barcode, canvasWidth / 2f, canvasHeight / 2f, 0f, canvasHeight)
                        } else {
                            val sourceRatio = rect.sourceWidth / rect.sourceHeight
                            val canvasRatio = canvasWidth / canvasHeight
                            val scale: Float
                            val offX: Float
                            val offY: Float
                            if (sourceRatio > canvasRatio) {
                                scale = canvasHeight / rect.sourceHeight
                                offX = -((rect.sourceWidth * scale) - canvasWidth) / 2f
                                offY = 0f
                            } else {
                                scale = canvasWidth / rect.sourceWidth
                                offX = 0f
                                offY = -((rect.sourceHeight * scale) - canvasHeight) / 2f
                            }

                            var minX = Float.MAX_VALUE
                            var minY = Float.MAX_VALUE
                            var maxX = -Float.MAX_VALUE
                            var maxY = -Float.MAX_VALUE

                            if (!barcode.cornerPoints.isNullOrEmpty() && barcode.cornerPoints.size == 4) {
                                barcode.cornerPoints.forEach { p ->
                                    val px = (p.x * scale) + offX
                                    val py = (p.y * scale) + offY
                                    if (px < minX) minX = px
                                    if (py < minY) minY = py
                                    if (px > maxX) maxX = px
                                    if (py > maxY) maxY = py
                                }
                            } else {
                                minX = (rect.left * scale) + offX
                                minY = (rect.top * scale) + offY
                                maxX = (rect.right * scale) + offX
                                maxY = (rect.bottom * scale) + offY
                            }

                            CardAnchor(
                                barcode = barcode,
                                anchorCx = (minX + maxX) / 2f,
                                anchorCy = (minY + maxY) / 2f,
                                barcodeTop = minY,
                                barcodeBottom = maxY,
                            )
                        }
                    }
                    val safeTopMargin = 170f
                    val gap = 10f
                    val maxW = constraints.maxWidth.toFloat()
                    val maxH = constraints.maxHeight.toFloat()

                    data class CardSlot(var x: Float, var y: Float, val w: Float, val h: Float) {
                        val right get() = x + w
                        val bottom get() = y + h
                        fun overlaps(other: CardSlot, margin: Float = gap / 2f): Boolean =
                            x < other.right + margin && right > other.x - margin &&
                            y < other.bottom + margin && bottom > other.y - margin
                    }

                    val slots = anchors.mapIndexed { index, anchor ->
                        val placeable = placeables.getOrNull(index)
                            ?: return@mapIndexed CardSlot(0f, 0f, 0f, 0f)
                        val w = placeable.width.toFloat()
                        val h = placeable.height.toFloat()
                        val idealX = (anchor.anchorCx - w / 2f).coerceIn(0f, (maxW - w).coerceAtLeast(0f))
                        val idealY = if (anchor.barcodeTop - h - gap >= safeTopMargin) {
                            anchor.barcodeTop - h - gap
                        } else {
                            anchor.barcodeBottom + gap
                        }

                        CardSlot(idealX, idealY.coerceIn(safeTopMargin, (maxH - h).coerceAtLeast(safeTopMargin)), w, h)
                    }
                    val maxRounds = 8
                    repeat(maxRounds) {
                        var anyOverlap = false
                        for (i in slots.indices) {
                            for (j in i + 1 until slots.size) {
                                val a = slots[i]
                                val b = slots[j]
                                if (!a.overlaps(b)) continue
                                anyOverlap = true
                                val overlapX = minOf(a.right, b.right) - maxOf(a.x, b.x) + gap
                                val overlapY = minOf(a.bottom, b.bottom) - maxOf(a.y, b.y) + gap

                                if (overlapX <= overlapY) {
                                    val half = overlapX / 2f
                                    val anchorI = anchors.getOrNull(i)?.anchorCx ?: 0f
                                    val anchorJ = anchors.getOrNull(j)?.anchorCx ?: 0f
                                    if (anchorI <= anchorJ) {
                                        slots[i].x = (a.x - half).coerceAtLeast(0f)
                                        slots[j].x = (b.x + half).coerceAtMost(maxW - b.w)
                                    } else {
                                        slots[i].x = (a.x + half).coerceAtMost(maxW - a.w)
                                        slots[j].x = (b.x - half).coerceAtLeast(0f)
                                    }
                                } else {
                                    val half = overlapY / 2f
                                    if (a.y <= b.y) {
                                        slots[i].y = (a.y - half).coerceAtLeast(safeTopMargin)
                                        slots[j].y = (b.y + half).coerceAtMost(maxH - b.h)
                                    } else {
                                        slots[i].y = (a.y + half).coerceAtMost(maxH - a.h)
                                        slots[j].y = (b.y - half).coerceAtLeast(safeTopMargin)
                                    }
                                }
                            }
                        }
                        if (!anyOverlap) return@repeat
                    }

                    slots.forEachIndexed { index, slot ->
                        val placeable = placeables.getOrNull(index) ?: return@forEachIndexed
                        placeable.placeRelative(slot.x.roundToInt(), slot.y.roundToInt())
                    }
                }
            }
        }
    }
}
