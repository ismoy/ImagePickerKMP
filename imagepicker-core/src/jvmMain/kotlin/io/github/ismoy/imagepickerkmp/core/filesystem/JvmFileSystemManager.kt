package io.github.ismoy.imagepickerkmp.core.filesystem

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class JvmFileSystemManager(
    private val subDirectory: String = "core_temp"
) : FileSystemManager {

    private fun cacheDir(): File =
        File(System.getProperty("java.io.tmpdir"), subDirectory).also { it.mkdirs() }

    override suspend fun createTempFile(prefix: String, suffix: String): PlatformFile =
        withContext(Dispatchers.IO) {
            val effectiveSuffix = suffix.ifEmpty { ".tmp" }
            val file = File.createTempFile(prefix, effectiveSuffix, cacheDir())
            PlatformFile(file.path)
        }

    override suspend fun getCacheDirectory(): PlatformFile =
        PlatformFile(cacheDir().path)

    override suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            cacheDir().listFiles()?.forEach { it.delete() }
        }
    }
}
