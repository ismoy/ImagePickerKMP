package io.github.ismoy.imagepickerkmp.scanner.utils

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

private const val BEEP_DURATION_MILLIS = 150L
private val mainHandler = Handler(Looper.getMainLooper())

actual fun playScannerSystemBeep() {
    val toneGenerator = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    } catch (exception: Exception) {
        LoggerFactory.getLogger().error("Audio", "Could not create system beep", exception)
        return
    }

    try {
        if (!toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, BEEP_DURATION_MILLIS.toInt())) {
            toneGenerator.release()
            LoggerFactory.getLogger().error("Audio", "Could not start system beep")
            return
        }
        mainHandler.postDelayed({ toneGenerator.release() }, BEEP_DURATION_MILLIS)
    } catch (exception: Exception) {
        toneGenerator.release()
        LoggerFactory.getLogger().error("Audio", "Could not play system beep", exception)
    }
}
