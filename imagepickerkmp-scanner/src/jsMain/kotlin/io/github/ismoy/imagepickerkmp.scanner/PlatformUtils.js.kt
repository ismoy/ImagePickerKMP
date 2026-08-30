package io.github.ismoy.imagepickerkmp.scanner

import androidx.compose.runtime.Composable

@Composable
actual fun getLocalContext(): Any? = null

@Composable
actual fun getLocalLifecycleOwner(): Any? = null

actual fun openAppSettings(context: Any?) {
    // No-op for JS
}
