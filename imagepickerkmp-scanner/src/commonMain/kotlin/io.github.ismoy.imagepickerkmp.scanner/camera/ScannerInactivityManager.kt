package io.github.ismoy.imagepickerkmp.scanner.camera

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.utils.getCurrentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/** Owns the inactivity timer for one scanner session. */
internal class ScannerInactivityManager(
    private val config: ScannerCameraConfig,
    private val onInactivity: () -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var inactivityJob: Job? = null
    private var lastActivityTime = 0L
    private var timerGeneration = 0L

    fun updateActivity(
        isCameraInactive: Boolean,
        onReactivate: () -> Unit
    ) {
        val currentTime = getCurrentTimeMillis()
        if (isCameraInactive) {
            onReactivate()
            lastActivityTime = currentTime
        } else if (currentTime - lastActivityTime > ACTIVITY_DEBOUNCE_MILLIS) {
            lastActivityTime = currentTime
            resetInactivityTimer()
        }
    }

    fun resetInactivityTimer() {
        cancelTimer()
        if (!config.behavior.enableInactivity) return

        val generation = timerGeneration
        inactivityJob = scope.launch {
            delay(config.behavior.inactivityDelay.milliseconds)
            if (generation == timerGeneration) {
                inactivityJob = null
                onInactivity()
            }
        }
    }

    fun cancelTimer() {
        timerGeneration++
        inactivityJob?.cancel()
        inactivityJob = null
    }

    fun dispose() {
        cancelTimer()
        scope.cancel()
    }

    private companion object {
        const val ACTIVITY_DEBOUNCE_MILLIS = 1_000L
    }
}
