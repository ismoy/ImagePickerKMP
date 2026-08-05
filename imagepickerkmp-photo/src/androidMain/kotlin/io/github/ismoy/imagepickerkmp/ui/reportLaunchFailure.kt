package io.github.ismoy.imagepickerkmp.ui

internal fun reportLaunchFailure(
    exception: Exception,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit
) {
    onError(exception)
    onDismiss()
}
