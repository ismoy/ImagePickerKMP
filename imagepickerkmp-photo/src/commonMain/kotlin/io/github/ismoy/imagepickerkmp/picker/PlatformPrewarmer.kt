package io.github.ismoy.imagepickerkmp.picker

/**
 * Platform-specific pre-warming to ensure instant camera and gallery loading.
 */
internal expect object PlatformPrewarmer {
    fun prewarm()
}
