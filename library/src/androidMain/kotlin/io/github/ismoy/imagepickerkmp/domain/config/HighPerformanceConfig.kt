package io.github.ismoy.imagepickerkmp.domain.config

import android.os.Build

/**
 * High-performance configuration optimizations for flagship Android devices.
 * 
 * This object provides optimized settings for devices like Galaxy S24 Ultra
 * and other high-end Android devices to improve camera capture performance.
 */
object HighPerformanceConfig {
    

    fun isHighEndDevice(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                (Build.HARDWARE?.contains("qcom", ignoreCase = true) == true ||
                 Build.HARDWARE?.contains("exynos", ignoreCase = true) == true ||
                 Build.MODEL?.contains("S24", ignoreCase = true) == true ||
                 Build.MODEL?.contains("S23", ignoreCase = true) == true ||
                 Build.MODEL?.contains("Pixel", ignoreCase = true) == true)
    }

    /**
     * Gets the optimal JPEG quality for high-end devices.
     * Balances quality vs performance for flagship devices.
     */
    fun getOptimalJpegQuality(): Int {
        return if (isHighEndDevice()) {
            95
        } else {
            85
        }
    }
}
