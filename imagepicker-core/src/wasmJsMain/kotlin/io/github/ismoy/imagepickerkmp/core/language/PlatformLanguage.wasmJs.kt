package io.github.ismoy.imagepickerkmp.core.language

@JsFun("() => window.navigator.language")
private external fun getBrowserLanguage(): String

actual fun getLanguageDevice(): String {
    val lang = getBrowserLanguage()
    return lang.take(2)
}