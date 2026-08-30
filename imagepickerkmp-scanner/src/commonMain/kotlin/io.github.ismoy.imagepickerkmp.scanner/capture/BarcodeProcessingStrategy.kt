package io.github.ismoy.imagepickerkmp.scanner.capture

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerAdvancedFeaturesConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.model.CameraPositionDistance

internal class BarcodeProcessingStrategy(
    private val behaviorConfig: ScannerBehaviorConfig,
    private val advancedConfig: ScannerAdvancedFeaturesConfig
) {
    private val lastScannedTimes = mutableMapOf<String, Long>()
    private val consecutiveReadings = mutableMapOf<String, Int>()
    private val distanceBuffer = mutableListOf<CameraPositionDistance>()

    private var lastEmittedValue: String? = null
    private var lastEmittedTime: Long = 0L

    fun isPhantomOfLastEmit(candidate: String, currentTimeMillis: Long): Boolean {
        val last = lastEmittedValue ?: return false
        if (candidate == last) return false
        if (currentTimeMillis - lastEmittedTime > behaviorConfig.maliciousCodeCooldown) return false
        if (kotlin.math.abs(candidate.length - last.length) > 3) return false

        val minimumLength = minOf(candidate.length, last.length)
        var differences = kotlin.math.abs(candidate.length - last.length)
        for (index in 0 until minimumLength) {
            if (candidate[index] != last[index]) differences++
            if (differences > 2) return false
        }
        return true
    }

    fun recordEmit(value: String, currentTimeMillis: Long) {
        lastEmittedValue = value
        lastEmittedTime = currentTimeMillis
    }

    fun updateDistance(newDistance: CameraPositionDistance) {
        distanceBuffer.add(newDistance)
        if (distanceBuffer.size > behaviorConfig.distanceBufferSize) {
            distanceBuffer.removeAt(0)
        }
    }

    fun getSmoothedDistance(): CameraPositionDistance {
        if (distanceBuffer.isEmpty()) return CameraPositionDistance.TOO_FAR
        return distanceBuffer
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: CameraPositionDistance.TOO_FAR
    }

    fun calculateDistance(areaRatio: Float): CameraPositionDistance = when {
        areaRatio < behaviorConfig.areaRatioThreshold -> CameraPositionDistance.TOO_FAR
        areaRatio > behaviorConfig.tooCloseAreaRatioThreshold -> CameraPositionDistance.TOO_CLOSE
        else -> CameraPositionDistance.OPTIMAL
    }

    fun isValidFormat(format: BarcodeFormat, rawValue: String): Boolean {
        if (rawValue.length > behaviorConfig.maxCodeLength) return false
        if (!isSecurityAllowed(rawValue)) return false
        return if (behaviorConfig.allowedFormats.contains(BarcodeFormat.ALL)) {
            format != BarcodeFormat.UNKNOWN
        } else {
            behaviorConfig.allowedFormats.contains(format)
        }
    }

    fun shouldEmit(value: String, currentTimeMillis: Long): Boolean {
        pruneExpiredScanTimes(currentTimeMillis)

        val distance = getSmoothedDistance()
        val distanceOk = distance == CameraPositionDistance.OPTIMAL || advancedConfig.batchMode
        if (!distanceOk) return false

        val readings = (consecutiveReadings[value] ?: 0) + 1
        consecutiveReadings[value] = readings

        val lastScanTime = lastScannedTimes[value] ?: 0L
        val cooldownElapsed = currentTimeMillis - lastScanTime > behaviorConfig.delayToNextScan
        if (readings >= behaviorConfig.requiredConsecutiveReadings && cooldownElapsed) {
            lastScannedTimes[value] = currentTimeMillis
            consecutiveReadings[value] = 0
            return true
        }
        return false
    }

    fun isPickAndPackSuppressed(): Boolean =
        advancedConfig.batchMode && advancedConfig.enablePickAndPack

    fun reset() {
        lastScannedTimes.clear()
        consecutiveReadings.clear()
        distanceBuffer.clear()
        lastEmittedValue = null
        lastEmittedTime = 0L
        updateDistance(CameraPositionDistance.TOO_FAR)
    }

    fun retainVisibleCodes(visibleValues: Set<String>) {
        consecutiveReadings.keys.retainAll(visibleValues)
    }

    /**
     * Security alerts reject values dominated by control or invisible characters.
     * URL punctuation and normal printable barcode content remain valid.
     */
    private fun isSecurityAllowed(rawValue: String): Boolean {
        if (!behaviorConfig.enableSecurityAlerts || rawValue.isEmpty()) return true
        val suspiciousCharacters = rawValue.count { character ->
            character.code <= 31 || character.code in 127..159 ||
                character == '\u200B' || character == '\u200C' ||
                character == '\u200D' || character == '\uFEFF'
        }
        return suspiciousCharacters.toFloat() / rawValue.length <= behaviorConfig.maxSpecialCharRatio
    }

    private fun pruneExpiredScanTimes(currentTimeMillis: Long) {
        lastScannedTimes.entries.removeAll { (_, scannedAt) ->
            currentTimeMillis - scannedAt > behaviorConfig.delayToNextScan
        }
    }
}
