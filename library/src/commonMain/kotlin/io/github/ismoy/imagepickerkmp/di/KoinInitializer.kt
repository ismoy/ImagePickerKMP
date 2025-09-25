package io.github.ismoy.imagepickerkmp.di

import org.koin.core.KoinApplication

/**
 * Platform-specific Koin initialization.
 * Each platform implements this function to configure Koin with its specific requirements.
 */
expect fun initKoin(configuration: KoinApplication.() -> Unit = {})
