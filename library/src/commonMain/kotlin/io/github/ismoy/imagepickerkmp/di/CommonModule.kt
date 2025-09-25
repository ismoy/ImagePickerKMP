package io.github.ismoy.imagepickerkmp.di

import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.presentation.viewModel.ImagePickerViewModel
import org.koin.dsl.module

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
