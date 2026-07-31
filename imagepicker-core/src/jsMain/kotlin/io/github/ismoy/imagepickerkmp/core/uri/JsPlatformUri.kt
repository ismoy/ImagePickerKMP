package io.github.ismoy.imagepickerkmp.core.uri

actual class PlatformUri(actual val rawValue: String) {

    actual override fun toString(): String = rawValue

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatformUri) return false
        return rawValue == other.rawValue
    }

    actual override fun hashCode(): Int = rawValue.hashCode()
}

actual fun platformUriFrom(rawValue: String): PlatformUri = PlatformUri(rawValue)
