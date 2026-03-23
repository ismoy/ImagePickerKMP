package io.github.ismoy.imagepickerkmp.domain.config

import android.app.ActivityManager
import android.content.Context
import android.os.Build

internal object HighPerformanceConfig {

    fun isHighEndDevice(context: Context? = null): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false

        if (context != null) {
            return try {
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val info = ActivityManager.MemoryInfo()
                am?.getMemoryInfo(info)
                if (info.totalMem > 0L) {
                    (info.totalMem / (1024L * 1024L * 1024L)) >= 4L
                } else {
                    hasFlagshipSoC()
                }
            } catch (_: Exception) {
                hasFlagshipSoC()
            }
        }
        return hasFlagshipSoC()
    }

    private fun hasFlagshipSoC(): Boolean {
        val hw = Build.HARDWARE?.lowercase() ?: return false
        return hw.startsWith("qcom") ||
            hw.startsWith("exynos") ||
            hw.contains("tensor") ||
            hw.startsWith("kirin") ||
            hw.startsWith("mt68") ||
            hw.startsWith("dimensity")
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
