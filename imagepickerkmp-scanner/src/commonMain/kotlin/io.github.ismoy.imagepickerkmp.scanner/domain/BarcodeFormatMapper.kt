package io.github.ismoy.imagepickerkmp.scanner.domain

interface BarcodeFormatMapper<T> {
    fun registerMapping(nativeFormat: T, domainFormat: BarcodeFormat)
    fun getDomainFormat(nativeFormat: T): BarcodeFormat
    fun getNativeFormat(domainFormat: BarcodeFormat): T?
}

open class BaseBarcodeFormatMapper<T> : BarcodeFormatMapper<T> {
    private val nativeToDomain = mutableMapOf<T, BarcodeFormat>()
    private val domainToNative = mutableMapOf<BarcodeFormat, T>()

    override fun registerMapping(nativeFormat: T, domainFormat: BarcodeFormat) {
        nativeToDomain[nativeFormat] = domainFormat
        domainToNative[domainFormat] = nativeFormat
    }

    override fun getDomainFormat(nativeFormat: T): BarcodeFormat {
        return nativeToDomain[nativeFormat] ?: BarcodeFormat.UNKNOWN
    }

    override fun getNativeFormat(domainFormat: BarcodeFormat): T? {
        return domainToNative[domainFormat]
    }
}
