package io.github.ismoy.imagepickerkmp.domain.utils

import android.media.MediaActionSound

fun playShutterSound() {
    val sound = MediaActionSound()
    sound.play(MediaActionSound.SHUTTER_CLICK)
}
