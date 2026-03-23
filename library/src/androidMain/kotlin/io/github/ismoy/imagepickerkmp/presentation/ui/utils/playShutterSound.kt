package io.github.ismoy.imagepickerkmp.presentation.ui.utils

import android.media.MediaActionSound

private val shutterSound: MediaActionSound by lazy { MediaActionSound() }

internal fun playShutterSound() {
    shutterSound.play(MediaActionSound.SHUTTER_CLICK)
}
