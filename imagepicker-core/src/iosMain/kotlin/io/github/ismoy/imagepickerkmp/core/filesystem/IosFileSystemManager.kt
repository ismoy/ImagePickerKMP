package io.github.ismoy.imagepickerkmp.core.filesystem

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSTemporaryDirectory

@OptIn(ExperimentalForeignApi::class)
internal class IosFileSystemManager(
    private val subDirectory: String = "core_temp"
) : FileSystemManager {
    private fun cacheDirPath(): String {
        val base = NSTemporaryDirectory() + subDirectory
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = base,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
        return base
    }

    override suspend fun createTempFile(prefix: String, suffix: String): PlatformFile {
        val effectiveSuffix = suffix.ifEmpty { ".tmp" }
        val unique = NSProcessInfo.processInfo.globallyUniqueString
        val path = "${cacheDirPath()}/$prefix$unique$effectiveSuffix"
        NSFileManager.defaultManager.createFileAtPath(path, contents = null, attributes = null)
        return PlatformFile(path)
    }

    override suspend fun getCacheDirectory(): PlatformFile =
        PlatformFile(cacheDirPath())

    override suspend fun clearCache() {
        val dir = cacheDirPath()
        val contents = NSFileManager.defaultManager.contentsOfDirectoryAtPath(dir, error = null)
        contents?.forEach { item ->
            NSFileManager.defaultManager.removeItemAtPath("$dir/$item", error = null)
        }
    }
}
