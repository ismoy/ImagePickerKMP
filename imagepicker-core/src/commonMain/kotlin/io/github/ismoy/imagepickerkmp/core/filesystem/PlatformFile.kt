package io.github.ismoy.imagepickerkmp.core.filesystem

expect class PlatformFile {
    val name: String
    val path: String
    val size: Long
    val exists: Boolean
}
