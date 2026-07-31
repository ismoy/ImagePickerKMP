package io.github.ismoy.imagepickerkmp.core.uri

expect class PlatformUri {
    val rawValue: String

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

expect fun platformUriFrom(rawValue: String): PlatformUri
