package io.github.ismoy.imagepickerkmp.scanner

class JsStaticCodeScanner : StaticCodeScanner {
    override suspend fun scanImage(imageBytes: ByteArray): String? {
        throw UnsupportedOperationException(
            "Static barcode scanning is supported only on Android and iOS."
        )
    }
}

actual fun createStaticCodeScanner(): StaticCodeScanner = JsStaticCodeScanner()
