package io.github.ismoy.imagepickerkmp

import android.app.Application
import io.github.ismoy.imagepickerkmp.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Main Application class for Android module initialization.
 * Initializes Koin dependency injection with Android context.
 */
class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApp)
        }
    }
}