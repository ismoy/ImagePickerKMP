package io.github.ismoy.imagepickerkmp.core.language

import kotlinx.browser.window

actual fun getLanguageDevice(): String {
    val lang = window.navigator.language
    return lang.take(2)
}