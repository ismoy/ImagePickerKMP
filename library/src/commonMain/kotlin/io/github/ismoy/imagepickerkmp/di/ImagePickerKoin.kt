package io.github.ismoy.imagepickerkmp.di

import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.presentation.viewModel.ImagePickerViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Simplified Koin setup for ImagePickerKMP.
 * 
 * SOLID: Single Responsibility - Only handles DI configuration
 */

/**
 * Common module with shared dependencies.
 */
val imagePickerCommonModule = module {
    single<ImagePickerLogger> { DefaultLogger }
    
    factory {
        ImagePickerViewModel(
            logger = get()
        )
    }
}

/**
 * Initialize Koin with modules.
 */
fun initImagePickerKoin(
    platformModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(
        buildList {
            add(imagePickerCommonModule)
            addAll(platformModules)
        }
    )
}
