package io.github.ismoy.imagepickerkmp.scanner

interface StaticCodeScanner {
    suspend fun scanImage(imageBytes: ByteArray): String?
}

expect fun createStaticCodeScanner(): StaticCodeScanner
