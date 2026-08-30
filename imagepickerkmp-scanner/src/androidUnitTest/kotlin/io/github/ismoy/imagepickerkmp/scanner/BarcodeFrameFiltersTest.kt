package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.BarcodeData
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BarcodeFrameFiltersTest {
    @Test
    fun stabilityTracker_requiresConsecutiveStableFrames_andResetsOnMovementOrRemoval() {
        val tracker = BarcodeBoundingBoxStabilityTracker()

        assertFalse(tracker.isStable("code", 0f, 0f))
        assertTrue(tracker.isStable("code", 0.1f, 0.1f))

        assertFalse(tracker.isStable("code", 0.3f, 0.1f))
        assertTrue(tracker.isStable("code", 0.3f, 0.1f))

        tracker.retainVisible(emptySet())
        assertFalse(tracker.isStable("code", 0.3f, 0.1f))

        tracker.reset()
        assertFalse(tracker.isStable("code", 0.3f, 0.1f))
    }

    @Test
    fun spatialDeduplicator_dropsMissingBoundsAndOverlappingPhysicalCodes() {
        val missingBounds = BarcodeData("missing", BarcodeFormat.QR_CODE, null, null)
        val first = barcode("first", 0f, 0f, 10f, 10f)
        val overlapping = barcode("overlapping", 1f, 1f, 11f, 11f)
        val separate = barcode("separate", 20f, 20f, 30f, 30f)

        val result = BarcodeSpatialDeduplicator.distinctPhysicalBarcodes(
            listOf(missingBounds, first, overlapping, separate)
        )

        assertEquals(listOf(first, separate), result)
    }

    private fun barcode(
        rawValue: String,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ): BarcodeData = BarcodeData(
        rawValue = rawValue,
        format = BarcodeFormat.QR_CODE,
        boundingBox = ScannerRect(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
            sourceWidth = 100f,
            sourceHeight = 100f,
            rotation = 0
        ),
        cornerPoints = null
    )
}
