package io.github.ismoy.imagepickerkmp.core.filesystem

interface FileSystemManager {
    suspend fun createTempFile(prefix: String = "tmp", suffix: String = ""): PlatformFile
    suspend fun getCacheDirectory(): PlatformFile
    suspend fun clearCache()
}
