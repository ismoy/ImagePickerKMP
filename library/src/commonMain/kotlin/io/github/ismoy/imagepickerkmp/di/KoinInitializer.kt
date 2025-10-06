package io.github.ismoy.imagepickerkmp.di

/**
 * Configuration interface for dependency injection.
 * For platforms with Koin, this will be KoinApplication.
 * For platforms without Koin (JS/WASM), this will be a stub.
 */
expect class KoinConfiguration

/**
 * Platform-specific Koin initialization.
 * Each platform implements this function to configure Koin with its specific requirements.
 * Note: For JS/WASM platforms, this is a stub implementation since Koin is not available.
 */
expect fun initKoin(configuration: KoinConfiguration.() -> Unit = {})
