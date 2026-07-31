package io.github.ismoy.imagepickerkmp.domain.models

import io.github.ismoy.imagepickerkmp.picker.ExifData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExifDataTest {

    // ── Construction ──────────────────────────────────────────────────────────

    @Test
    fun defaultConstruction_allFieldsAreNull() {
        val exif = ExifData()
        assertNull(exif.latitude)
        assertNull(exif.longitude)
        assertNull(exif.altitude)
        assertNull(exif.dateTaken)
        assertNull(exif.dateTime)
        assertNull(exif.digitizedTime)
        assertNull(exif.originalTime)
        assertNull(exif.modifiedTime)
        assertNull(exif.utcTime)
        assertNull(exif.cameraModel)
        assertNull(exif.cameraManufacturer)
        assertNull(exif.cameraMake)
        assertNull(exif.software)
        assertNull(exif.owner)
        assertNull(exif.orientation)
        assertNull(exif.colorSpace)
        assertNull(exif.whiteBalance)
        assertNull(exif.flash)
        assertNull(exif.focalLength)
        assertNull(exif.aperture)
        assertNull(exif.shutterSpeed)
        assertNull(exif.iso)
        assertNull(exif.exposureBias)
        assertNull(exif.meteringMode)
        assertNull(exif.sceneCaptureType)
        assertNull(exif.imageWidth)
        assertNull(exif.imageHeight)
        assertNull(exif.xResolution)
        assertNull(exif.yResolution)
        assertNull(exif.resolutionUnit)
        assertNull(exif.compression)
        assertNull(exif.cloudCache)
        assertNull(exif.thumbnail)
    }

    @Test
    fun gpsFields_setCorrectly() {
        val exif = ExifData(latitude = 48.8566, longitude = 2.3522, altitude = 35.0)
        assertEquals(48.8566, exif.latitude)
        assertEquals(2.3522, exif.longitude)
        assertEquals(35.0, exif.altitude)
    }

    @Test
    fun cameraFields_setCorrectly() {
        val exif = ExifData(
            cameraModel = "Pixel 8 Pro",
            cameraManufacturer = "Google",
            cameraMake = "Google",
            software = "HDR+ 1.0"
        )
        assertEquals("Pixel 8 Pro", exif.cameraModel)
        assertEquals("Google", exif.cameraManufacturer)
        assertEquals("Google", exif.cameraMake)
        assertEquals("HDR+ 1.0", exif.software)
    }

    @Test
    fun dateTimeFields_setCorrectly() {
        val exif = ExifData(
            dateTaken = "2024:01:01 12:00:00",
            dateTime = "2024:01:01 12:00:00",
            digitizedTime = "2024:01:01 12:00:01",
            originalTime = "2024:01:01 12:00:00",
            modifiedTime = "2024:01:02 08:30:00",
            utcTime = "2024-01-01T12:00:00Z"
        )
        assertEquals("2024:01:01 12:00:00", exif.dateTaken)
        assertEquals("2024:01:01 12:00:00", exif.dateTime)
        assertEquals("2024:01:01 12:00:01", exif.digitizedTime)
        assertEquals("2024:01:01 12:00:00", exif.originalTime)
        assertEquals("2024:01:02 08:30:00", exif.modifiedTime)
        assertEquals("2024-01-01T12:00:00Z", exif.utcTime)
    }

    @Test
    fun imageProperties_setCorrectly() {
        val exif = ExifData(
            orientation = "1",
            colorSpace = "sRGB",
            whiteBalance = "Auto",
            flash = "No Flash",
            focalLength = "4.44mm",
            aperture = "f/1.8",
            shutterSpeed = "1/100",
            iso = "100",
            exposureBias = "0 EV",
            meteringMode = "Pattern",
            sceneCaptureType = "Standard"
        )
        assertEquals("1", exif.orientation)
        assertEquals("sRGB", exif.colorSpace)
        assertEquals("Auto", exif.whiteBalance)
        assertEquals("No Flash", exif.flash)
        assertEquals("4.44mm", exif.focalLength)
        assertEquals("f/1.8", exif.aperture)
        assertEquals("1/100", exif.shutterSpeed)
        assertEquals("100", exif.iso)
        assertEquals("0 EV", exif.exposureBias)
        assertEquals("Pattern", exif.meteringMode)
        assertEquals("Standard", exif.sceneCaptureType)
    }

    @Test
    fun technicalFields_setCorrectly() {
        val exif = ExifData(
            imageWidth = 4032,
            imageHeight = 3024,
            xResolution = "72",
            yResolution = "72",
            resolutionUnit = "Inch",
            compression = "JPEG",
            cloudCache = "none",
            thumbnail = "base64encodeddata"
        )
        assertEquals(4032, exif.imageWidth)
        assertEquals(3024, exif.imageHeight)
        assertEquals("72", exif.xResolution)
        assertEquals("72", exif.yResolution)
        assertEquals("Inch", exif.resolutionUnit)
        assertEquals("JPEG", exif.compression)
        assertEquals("none", exif.cloudCache)
        assertEquals("base64encodeddata", exif.thumbnail)
    }

    // ── withRedactedGps ───────────────────────────────────────────────────────

    @Test
    fun withRedactedGps_setsGpsFieldsToNull() {
        val exif = ExifData(
            latitude = 48.8566,
            longitude = 2.3522,
            altitude = 35.0,
            cameraModel = "Pixel 8",
            dateTaken = "2024:01:01 12:00:00"
        )
        val redacted = exif.withRedactedGps()
        assertNull(redacted.latitude)
        assertNull(redacted.longitude)
        assertNull(redacted.altitude)
    }

    @Test
    fun withRedactedGps_preservesAllNonGpsFields() {
        val exif = ExifData(
            latitude = 48.8566,
            longitude = 2.3522,
            altitude = 35.0,
            cameraModel = "Pixel 8",
            cameraManufacturer = "Google",
            dateTaken = "2024:01:01 12:00:00",
            iso = "200",
            aperture = "f/2.0",
            imageWidth = 4032,
            imageHeight = 3024,
            thumbnail = "thumb"
        )
        val redacted = exif.withRedactedGps()
        assertEquals("Pixel 8", redacted.cameraModel)
        assertEquals("Google", redacted.cameraManufacturer)
        assertEquals("2024:01:01 12:00:00", redacted.dateTaken)
        assertEquals("200", redacted.iso)
        assertEquals("f/2.0", redacted.aperture)
        assertEquals(4032, redacted.imageWidth)
        assertEquals(3024, redacted.imageHeight)
        assertEquals("thumb", redacted.thumbnail)
    }

    @Test
    fun withRedactedGps_onDefaultExif_remainsAllNull() {
        val exif = ExifData()
        val redacted = exif.withRedactedGps()
        assertNull(redacted.latitude)
        assertNull(redacted.longitude)
        assertNull(redacted.altitude)
        assertNull(redacted.cameraModel)
    }

    @Test
    fun withRedactedGps_doesNotMutateOriginal() {
        val exif = ExifData(latitude = 48.8566, longitude = 2.3522, altitude = 10.0)
        exif.withRedactedGps()
        assertEquals(48.8566, exif.latitude)
        assertEquals(2.3522, exif.longitude)
        assertEquals(10.0, exif.altitude)
    }

    // ── Equality & hashCode ───────────────────────────────────────────────────

    @Test
    fun equality_sameValues_areEqual() {
        val a = ExifData(cameraModel = "Pixel 8", iso = "100")
        val b = ExifData(cameraModel = "Pixel 8", iso = "100")
        assertEquals(a, b)
    }

    @Test
    fun equality_differentValues_notEqual() {
        val a = ExifData(cameraModel = "Pixel 8")
        val b = ExifData(cameraModel = "iPhone 15")
        assertTrue(a != b)
    }

    @Test
    fun hashCode_sameForEqualInstances() {
        val a = ExifData(latitude = 1.0, longitude = 2.0)
        val b = ExifData(latitude = 1.0, longitude = 2.0)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // ── Copy ─────────────────────────────────────────────────────────────────

    @Test
    fun copy_changesOnlySpecifiedField() {
        val original = ExifData(cameraModel = "Pixel 8", iso = "100")
        val copy = original.copy(iso = "200")
        assertEquals("200", copy.iso)
        assertEquals("Pixel 8", copy.cameraModel)
    }

    @Test
    fun copy_doesNotMutateOriginal() {
        val original = ExifData(cameraModel = "Pixel 8")
        original.copy(cameraModel = "iPhone 15")
        assertEquals("Pixel 8", original.cameraModel)
    }

    // ── GPS coordinate edge cases ─────────────────────────────────────────────

    @Test
    fun gps_negativeCoordinates_storedCorrectly() {
        val exif = ExifData(latitude = -33.8688, longitude = -70.6693, altitude = -5.0)
        assertEquals(-33.8688, exif.latitude)
        assertEquals(-70.6693, exif.longitude)
        assertEquals(-5.0, exif.altitude)
    }

    @Test
    fun gps_zeroCoordinates_storedCorrectly() {
        val exif = ExifData(latitude = 0.0, longitude = 0.0, altitude = 0.0)
        assertEquals(0.0, exif.latitude)
        assertEquals(0.0, exif.longitude)
        assertEquals(0.0, exif.altitude)
    }

    @Test
    fun gps_extremeCoordinates_storedCorrectly() {
        val exif = ExifData(latitude = 90.0, longitude = 180.0, altitude = 8848.0)
        assertEquals(90.0, exif.latitude)
        assertEquals(180.0, exif.longitude)
        assertEquals(8848.0, exif.altitude)
    }

    // ── thumbnail field ───────────────────────────────────────────────────────

    @Test
    fun thumbnail_largeBase64String_storedCorrectly() {
        val bigThumb = "A".repeat(20_000)
        val exif = ExifData(thumbnail = bigThumb)
        assertNotNull(exif.thumbnail)
        assertEquals(20_000, exif.thumbnail!!.length)
    }

    @Test
    fun withRedactedGps_preservesThumbnail() {
        val thumb = "base64data"
        val exif = ExifData(latitude = 1.0, thumbnail = thumb)
        val redacted = exif.withRedactedGps()
        assertEquals(thumb, redacted.thumbnail)
    }
}
