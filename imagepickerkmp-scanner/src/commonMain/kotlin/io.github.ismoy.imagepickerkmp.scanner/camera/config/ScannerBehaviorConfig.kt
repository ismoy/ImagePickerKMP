package io.github.ismoy.imagepickerkmp.scanner.camera.config

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.HapticFeedbackMode
import io.github.ismoy.imagepickerkmp.scanner.domain.model.ScannerRect

data class ScannerBehaviorConfig(
    val playSound: Boolean = true,
    val hapticFeedback: HapticFeedbackMode = HapticFeedbackMode.SOUND_AND_VIBRATE,
    val soundResourceName: String = "beep",
    val soundResourceExtension: String = "mp3",
    val delayToNextScan: Long = 2000L,
    val areaRatioThreshold: Float = 0.01f,
    val tooCloseAreaRatioThreshold: Float = 0.4f,
    val requiredConsecutiveReadings: Int = 2,
    val distanceBufferSize: Int = 3,
    val maliciousCodeCooldown: Long = 3000L,
    val maxCodeLength: Int = 2000,
    val maxSpecialCharRatio: Float = 0.15f,
    val enableSecurityAlerts: Boolean = true,
    val enableFlashControl: Boolean = true,
    val showFlashButton: Boolean = true,
    val enableInactivity: Boolean = true,
    val inactivityDelay: Long = 30000L,
    val allowedFormats: List<BarcodeFormat> = listOf(BarcodeFormat.ALL),
    val enablePinchToZoom: Boolean = true,
    val enableTapToFocus: Boolean = true,
    val supportInvertedBarcodes: Boolean = true,
    val allowDuplicates: Boolean = false,
    val regionOfInterest: ScannerRect? = null
) {
    init {
        require(delayToNextScan >= 0) { "delayToNextScan must be non-negative." }
        require(areaRatioThreshold in 0f..1f) { "areaRatioThreshold must be between 0 and 1." }
        require(tooCloseAreaRatioThreshold in 0f..1f) {
            "tooCloseAreaRatioThreshold must be between 0 and 1."
        }
        require(areaRatioThreshold <= tooCloseAreaRatioThreshold) {
            "areaRatioThreshold cannot exceed tooCloseAreaRatioThreshold."
        }
        require(requiredConsecutiveReadings >= 1) {
            "requiredConsecutiveReadings must be at least 1."
        }
        require(distanceBufferSize >= 1) { "distanceBufferSize must be at least 1." }
        require(maliciousCodeCooldown >= 0) { "maliciousCodeCooldown must be non-negative." }
        require(maxCodeLength > 0) { "maxCodeLength must be greater than 0." }
        require(maxSpecialCharRatio in 0f..1f) {
            "maxSpecialCharRatio must be between 0 and 1."
        }
        require(inactivityDelay >= 0) { "inactivityDelay must be non-negative." }
        require(allowedFormats.isNotEmpty()) { "allowedFormats cannot be empty." }
    }
}
