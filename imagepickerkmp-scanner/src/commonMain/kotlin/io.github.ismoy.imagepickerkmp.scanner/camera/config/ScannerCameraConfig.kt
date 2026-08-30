package io.github.ismoy.imagepickerkmp.scanner.camera.config

data class ScannerCameraConfig(
    val behavior: ScannerBehaviorConfig = ScannerBehaviorConfig(),
    val advanced: ScannerAdvancedFeaturesConfig = ScannerAdvancedFeaturesConfig(),
    val ui: ScannerUIConfig = ScannerUIConfig()
) {
    companion object {
        fun default() = ScannerCameraConfig()

        fun minimal() = ScannerCameraConfig(
            behavior = ScannerBehaviorConfig(
                playSound = false,
                enableSecurityAlerts = false
            ),
            ui = ScannerUIConfig(
                watermark = ""
            )
        )

        fun highSecurity() = ScannerCameraConfig(
            behavior = ScannerBehaviorConfig(
                delayToNextScan = 3000L,
                requiredConsecutiveReadings = 3,
                maliciousCodeCooldown = 5000L,
                maxCodeLength = 1000,
                maxSpecialCharRatio = 0.10f
            )
        )
    }
}
