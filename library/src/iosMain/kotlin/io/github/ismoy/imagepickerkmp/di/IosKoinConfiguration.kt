
package io.github.ismoy.imagepickerkmp.di

import org.koin.dsl.module

val iosImagePickerModule = module {}

/**
 * iOS-specific Koin configuration.
 */
object IosImagePickerKoin {

    /**
     * Initializes Koin for iOS with platform modules.
     */
    fun init() {
        initImagePickerKoin(
            platformModules = listOf(iosImagePickerModule)
        )
    }
    
}
