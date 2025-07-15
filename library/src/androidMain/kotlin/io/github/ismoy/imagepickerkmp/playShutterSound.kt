

package io.github.ismoy.imagepickerkmp

import android.media.MediaActionSound

fun playShutterSound() {
    val sound = MediaActionSound()
    sound.play(MediaActionSound.SHUTTER_CLICK)
}
