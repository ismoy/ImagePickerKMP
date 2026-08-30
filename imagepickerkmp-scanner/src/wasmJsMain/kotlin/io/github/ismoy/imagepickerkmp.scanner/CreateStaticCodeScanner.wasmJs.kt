package io.github.ismoy.imagepickerkmp.scanner

class WasmJsStaticCodeScanner : StaticCodeScanner {
    override suspend fun scanImage(imageBytes: ByteArray): String? {
        throw UnsupportedOperationException(
            "Static barcode scanning is supported only on Android and iOS."
        )
    }
}

actual fun createStaticCodeScanner(): StaticCodeScanner = WasmJsStaticCodeScanner()
