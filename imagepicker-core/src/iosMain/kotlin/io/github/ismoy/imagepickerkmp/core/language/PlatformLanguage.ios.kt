package io.github.ismoy.imagepickerkmp.core.language

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun getLanguageDevice(): String {
    val preferred = NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
    return preferred.substringBefore("-").substringBefore("_")
}