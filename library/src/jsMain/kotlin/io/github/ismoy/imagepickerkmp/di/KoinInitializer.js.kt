package io.github.ismoy.imagepickerkmp.di

/**
 * Stub configuration class for JS platform (Koin is not available).
 */
actual class KoinConfiguration

/**
 * JS-specific Koin initialization.
 * Note: Koin is not available in JS platform, so this is a stub implementation.
 * Camera and gallery functionality is not available in JS platform.
 */
actual fun initKoin(configuration: KoinConfiguration.() -> Unit) {
    // Stub implementation - Koin is not available in JS
    // JS platform doesn't support dependency injection with Koin
}
