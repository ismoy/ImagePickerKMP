package io.github.ismoy.imagepickerkmp.domain.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExifDataTest {

    // ───────────── withRedactedGps ─────────────

    @Test
    fun withRedactedGps_nullifiesLatitudeLongitudeAltitude() {
        val exif = ExifData(
            latitude = 40.7128,
            longitude = -74.0060,
            altitude = 10.0,
            cameraModel = "Pixel 8",
            dateTaken = "2024-01-01"
        )
        val redacted = exif.withRedactedGps()
        assertNull(redacted.latitude)
        assertNull(redacted.longitude)
        assertNull(redacted.altitude)
    }

    @Test
    fun withRedactedGps_preservesAllOtherFields() {
        val exif = ExifData(
            latitude = 40.7128,
            longitude = -74.0060,
            altitude = 10.0,
            cameraModel = "Pixel 8",
            cameraManufacturer = "Google",
            dateTaken = "2024-01-01",
            iso = "100",
            aperture = "f/1.8",
            shutterSpeed = "1/500",
            orientation = "1",
            flash = "0",
            focalLength = "4.4mm"
        )
        val redacted = exif.withRedactedGps()
        assertEquals("Pixel 8", redacted.cameraModel)
        assertEquals("Google", redacted.cameraManufacturer)
        assertEquals("2024-01-01", redacted.dateTaken)
        assertEquals("100", redacted.iso)
        assertEquals("f/1.8", redacted.aperture)
        assertEquals("1/500", redacted.shutterSpeed)
        assertEquals("1", redacted.orientation)
    }

    @Test
    fun withRedactedGps_alreadyNullGps_remainsNull() {
        val exif = ExifData(cameraModel = "iPhone 15")
        val redacted = exif.withRedactedGps()
        assertNull(redacted.latitude)
        assertNull(redacted.longitude)
        assertNull(redacted.altitude)
        assertEquals("iPhone 15", redacted.cameraModel)
    }

    @Test
    fun withRedactedGps_doesNotMutateOriginal() {
        val original = ExifData(latitude = 48.8566, longitude = 2.3522, altitude = 35.0)
        original.withRedactedGps()
        assertNotNull(original.latitude, "Original latitude should not be mutated")
        assertNotNull(original.longitude, "Original longitude should not be mutated")
        assertNotNull(original.altitude, "Original altitude should not be mutated")
    }

    // ───────────── Default values ─────────────

    @Test
    fun defaultExifData_allFieldsAreNull() {
        val exif = ExifData()
        assertNull(exif.latitude)
        assertNull(exif.longitude)
        assertNull(exif.altitude)
        assertNull(exif.dateTaken)
        assertNull(exif.cameraModel)
        assertNull(exif.cameraManufacturer)
        assertNull(exif.iso)
        assertNull(exif.aperture)
        assertNull(exif.thumbnail)
    }

    // ───────────── Data class equality ─────────────

    @Test
    fun twoIdenticalExifData_areEqual() {
        val a = ExifData(latitude = 1.0, cameraModel = "Test")
        val b = ExifData(latitude = 1.0, cameraModel = "Test")
        assertEquals(a, b)
    }

    @Test
    fun differentExifData_areNotEqual() {
        val a = ExifData(latitude = 1.0)
        val b = ExifData(latitude = 2.0)
        assertTrue(a != b)
    }

    // ───────────── Camera metadata fields ─────────────

    @Test
    fun exifData_cameraFieldsAreStoredCorrectly() {
        val exif = ExifData(
            cameraModel = "Galaxy S24",
            cameraMake = "Samsung",
            software = "Android 14",
            imageWidth = 4080,
            imageHeight = 3060
        )
        assertEquals("Galaxy S24", exif.cameraModel)
        assertEquals("Samsung", exif.cameraMake)
        assertEquals("Android 14", exif.software)
        assertEquals(4080, exif.imageWidth)
        assertEquals(3060, exif.imageHeight)
    }

    // ───────────── Full GPS round-trip ─────────────

    @Test
    fun exifData_gpsFieldsStoredAndRetrievedCorrectly() {
        val lat = 35.6762
        val lon = 139.6503
        val alt = 40.5
        val exif = ExifData(latitude = lat, longitude = lon, altitude = alt)
        assertEquals(lat, exif.latitude!!, 0.0001)
        assertEquals(lon, exif.longitude!!, 0.0001)
        assertEquals(alt, exif.altitude!!, 0.0001)
    }
}
