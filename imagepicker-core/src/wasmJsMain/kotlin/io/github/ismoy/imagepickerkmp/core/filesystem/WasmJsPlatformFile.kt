package io.github.ismoy.imagepickerkmp.core.filesystem

actual class PlatformFile(actual val path: String) {
    actual val name: String get() = path.substringAfterLast('/')
    actual val size: Long get() = 0L
    actual val exists: Boolean get() = false
}
