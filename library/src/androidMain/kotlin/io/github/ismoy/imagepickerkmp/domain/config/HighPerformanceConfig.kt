package io.github.ismoy.imagepickerkmp.domain.config

import android.os.Build


object HighPerformanceConfig {
    

    fun isHighEndDevice(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                (Build.HARDWARE?.contains("qcom", ignoreCase = true) == true ||
                 Build.HARDWARE?.contains("exynos", ignoreCase = true) == true ||
                 Build.MODEL?.contains("S24", ignoreCase = true) == true ||
                 Build.MODEL?.contains("S23", ignoreCase = true) == true ||
                 Build.MODEL?.contains("Pixel", ignoreCase = true) == true)
    }

    fun requiresCompatibilityMode(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q 
    }

    fun getOptimalJpegQuality(): Int {
        return when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> 90 
            isHighEndDevice() -> 95
            else -> 85
        }
    }
}
