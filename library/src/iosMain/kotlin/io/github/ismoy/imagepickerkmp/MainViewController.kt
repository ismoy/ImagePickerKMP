package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.window.ComposeUIViewController
import io.github.ismoy.imagepickerkmp.di.initKoin

/**
 * Main view controller for iOS that initializes Koin dependency injection.
 * 
 * This controller should be set as the root view controller in your iOS app
 * to ensure proper initialization of the ImagePickerKMP library.
 */
fun MainViewController() = ComposeUIViewController (configure = { initKoin() }){}
