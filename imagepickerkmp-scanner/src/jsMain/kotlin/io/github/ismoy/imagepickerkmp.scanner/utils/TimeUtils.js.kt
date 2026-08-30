package io.github.ismoy.imagepickerkmp.scanner.utils

import kotlin.js.Date

actual fun getCurrentTimeMillis(): Long {
    return Date.now().toLong()
}
