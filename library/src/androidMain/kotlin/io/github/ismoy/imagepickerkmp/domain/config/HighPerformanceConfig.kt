package io.github.ismoy.imagepickerkmp.domain.config

import android.app.ActivityManager
import android.content.Context
import android.os.Build

internal object HighPerformanceConfig {

    fun isHighEndDevice(context: Context? = null): Boolean {
        val hasModernApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val hasPowerfulChip = Build.HARDWARE?.let { hw ->
            hw.contains("qcom", ignoreCase = true) ||
            hw.contains("exynos", ignoreCase = true) ||
            hw.contains("tensor", ignoreCase = true) ||
            hw.contains("kirin", ignoreCase = true) ||
            hw.contains("dimensity", ignoreCase = true) ||
            hw.contains("mt68", ignoreCase = true)
        } ?: false

        val hasEnoughRam = if (context != null) {
            try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                if (am != null) {
                    val info = ActivityManager.MemoryInfo()
                    am.getMemoryInfo(info)
                    if (info.totalMem > 0L) {
                        (info.totalMem / (1024L * 1024L * 1024L)) >= 4L
                    } else {
                        hasPowerfulChip
                    }
                } else {
                    hasPowerfulChip
                }
            } catch (_: Exception) {
                hasPowerfulChip
            }
        } else {
            hasPowerfulChip
        }

        return hasModernApi && (hasPowerfulChip || hasEnoughRam)
    }

    fun requiresCompatibilityMode(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    }

    fun getOptimalJpegQuality(context: Context? = null): Int {
        return when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> 90
            isHighEndDevice(context) -> 100
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> 95
            else -> 90
        }
    }

    fun getRecompressQuality(context: Context? = null): Int {
        return when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> 92
            isHighEndDevice(context) -> 98
            else -> 95
        }
    }
}
