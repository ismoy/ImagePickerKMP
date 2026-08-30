package io.github.ismoy.imagepickerkmp.scanner.utils

import platform.AudioToolbox.AudioServicesPlaySystemSound

actual fun playScannerSystemBeep() {
    AudioServicesPlaySystemSound(1052u)
}
