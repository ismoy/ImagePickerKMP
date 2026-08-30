package io.github.ismoy.imagepickerkmp.scanner

import androidx.compose.runtime.Composable

@Composable
expect fun getLocalContext(): Any?

@Composable
expect fun getLocalLifecycleOwner(): Any?

expect fun openAppSettings(context: Any?)