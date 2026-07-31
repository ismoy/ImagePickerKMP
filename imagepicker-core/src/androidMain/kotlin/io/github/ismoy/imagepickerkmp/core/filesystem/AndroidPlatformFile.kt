package io.github.ismoy.imagepickerkmp.core.filesystem

import java.io.File

actual class PlatformFile(private val filePath: String) {

    private val file: File = File(filePath)

    actual val name: String get() = file.name
    actual val path: String get() = file.path
    actual val size: Long get() = file.length()
    actual val exists: Boolean get() = file.exists()
}
