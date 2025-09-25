package io.github.ismoy.imagepickerkmp.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosImagePickerModule = module {
    // iOS-specific dependencies can be added here if needed
}

/**
 * iOS-specific Koin initialization.
 * Configures Koin with common modules and iOS-specific modules.
 */
actual fun initKoin(configuration: KoinApplication.() -> Unit) {
    startKoin {
        configuration(this)
        modules(imagePickerCommonModule, iosImagePickerModule)
    }
}
