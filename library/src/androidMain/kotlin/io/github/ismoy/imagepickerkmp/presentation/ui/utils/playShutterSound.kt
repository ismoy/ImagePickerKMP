package io.github.ismoy.imagepickerkmp.presentation.ui.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaActionSound

private val shutterSound: MediaActionSound by lazy { MediaActionSound() }

/**
 * Plays the shutter click sound, but only when the device ringer is in normal mode.
 * Silent and vibrate modes suppress the sound, matching stock camera app behavior.
 */
internal fun playShutterSound(context: Context) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    if (audioManager?.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
        shutterSound.play(MediaActionSound.SHUTTER_CLICK)
    }
}
