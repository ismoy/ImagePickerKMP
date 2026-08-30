package io.github.ismoy.imagepickerkmp.scanner

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresPermission
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.HapticFeedbackMode
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory

internal class AndroidScannerSoundManager(
    private val context: Context,
    private val config: ScannerCameraConfig
) {
    private var soundPool: SoundPool? = null
    private var scanBeepSound: Int = 0
    private val logger = LoggerFactory.getLogger()

    private val wantsSound: Boolean
        get() = config.behavior.playSound ||
            config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_AND_VIBRATE ||
            config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_ONLY

    init {
        ensureSoundPool()
    }

    private fun ensureSoundPool() {
        if (!wantsSound || soundPool != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        scanBeepSound = soundPool?.load(context, com.google.zxing.client.android.R.raw.zxing_beep, 1) ?: 0
        logger.info("Scanner", "Sound setup completed")
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playBeepSound() {
        if (wantsSound) {
            ensureSoundPool()
            if (scanBeepSound != 0) {
                soundPool?.play(scanBeepSound, 1f, 1f, 1, 0, 1f)
            }
        }

        val wantsVibrate = config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_AND_VIBRATE ||
            config.behavior.hapticFeedback == HapticFeedbackMode.VIBRATE_ONLY
        if (wantsVibrate) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator?.hasVibrator() == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(100)
                    }
                }
            } catch (exception: Exception) {
                logger.error("CameraX", "Error vibrating", exception)
            }
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        scanBeepSound = 0
    }
}
