package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect

/**
 * Rejects one-frame motion artifacts until a barcode's center remains spatially
 * stable across consecutive camera frames.
 */
internal class BarcodeBoundingBoxStabilityTracker {
    private val lastCenter = mutableMapOf<String, Pair<Float, Float>>()
    private val stableFrames = mutableMapOf<String, Int>()

    fun isStable(rawValue: String, centerX: Float, centerY: Float): Boolean {
        val previousCenter = lastCenter.put(rawValue, centerX to centerY)
        if (previousCenter == null) {
            stableFrames[rawValue] = 1
            return false
        }

        val moved = kotlin.math.abs(centerX - previousCenter.first) > MAX_CENTER_DISPLACEMENT_RATIO ||
            kotlin.math.abs(centerY - previousCenter.second) > MAX_CENTER_DISPLACEMENT_RATIO
        if (moved) {
            stableFrames[rawValue] = 1
            return false
        }

        val frames = (stableFrames[rawValue] ?: 1) + 1
        stableFrames[rawValue] = frames
        return frames >= REQUIRED_STABLE_FRAMES
    }

    fun retainVisible(visibleValues: Set<String>) {
        lastCenter.keys.retainAll(visibleValues)
        stableFrames.keys.retainAll(visibleValues)
    }

    fun reset() {
        lastCenter.clear()
        stableFrames.clear()
    }

    private companion object {
        const val REQUIRED_STABLE_FRAMES = 2
        const val MAX_CENTER_DISPLACEMENT_RATIO = 0.15f
    }
}

/** Removes multiple ML Kit observations of the same physical barcode in one frame. */
internal object BarcodeSpatialDeduplicator {
    fun distinctPhysicalBarcodes(barcodes: List<BarcodeData>): List<BarcodeData> {
        val result = mutableListOf<BarcodeData>()
        for (barcode in barcodes) {
            val bounds = barcode.boundingBox ?: continue
            if (result.none { existing ->
                    existing.boundingBox?.let { intersectionOverUnion(bounds, it) > IOU_THRESHOLD } == true
                }
            ) {
                result += barcode
            }
        }
        return result
    }

    private fun intersectionOverUnion(first: ScannerRect, second: ScannerRect): Float {
        val intersectionLeft = maxOf(first.left, second.left)
        val intersectionTop = maxOf(first.top, second.top)
        val intersectionRight = minOf(first.right, second.right)
        val intersectionBottom = minOf(first.bottom, second.bottom)
        if (intersectionLeft >= intersectionRight || intersectionTop >= intersectionBottom) return 0f

        val intersectionArea = (intersectionRight - intersectionLeft) *
            (intersectionBottom - intersectionTop)
        val unionArea = first.width * first.height + second.width * second.height - intersectionArea
        return if (unionArea > 0f) intersectionArea / unionArea else 0f
    }

    private const val IOU_THRESHOLD = 0.5f
}
