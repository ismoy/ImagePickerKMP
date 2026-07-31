package io.github.ismoy.imagepickerkmp.core.filesystem

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual class PlatformFile internal constructor(actual val path: String) {

    actual val name: String
        get() = NSURL.fileURLWithPath(path).lastPathComponent ?: path.substringAfterLast('/')

    actual val size: Long
        get() {
            val attrs = NSFileManager.defaultManager.attributesOfItemAtPath(path, error = null)
            return (attrs?.get("NSFileSize") as? Long) ?: 0L
        }

    actual val exists: Boolean
        get() = NSFileManager.defaultManager.fileExistsAtPath(path)
}
