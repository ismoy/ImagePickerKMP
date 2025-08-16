
package io.github.ismoy.imagepickerkmp.di

import org.koin.dsl.module

val iosImagePickerModule = module {
    
    // iOS classes are mostly objects or have callback-based constructors
    // that don't fit well with DI patterns. They can be used directly.
    
    // Future iOS-specific dependencies can be added here
    
}

/**
 * iOS-specific Koin configuration.
 * 
 * SOLID: Single Responsibility - Only handles iOS DI configuration
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
