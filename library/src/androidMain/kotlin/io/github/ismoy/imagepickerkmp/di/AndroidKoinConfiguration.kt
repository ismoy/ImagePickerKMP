
package io.github.ismoy.imagepickerkmp.di

import android.content.Context
import androidx.activity.ComponentActivity
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val androidImagePickerModule = module {
    
    factory { (context: Context) ->
        FileManager(context) 
    }
    
    factory { ImageOrientationCorrector() }
    
    factory { (context: Context) -> 
        ImageProcessor(
            fileManager = get { parametersOf(context) },
            orientationCorrector = get()
        )
    }
    
    factory { (context: Context, activity: ComponentActivity) ->
        CameraController(context, activity) 
    }
    
    factory { (context: Context, activity: ComponentActivity) -> 
        CameraXManager(
            cameraController = get { parametersOf(context, activity) },
            imageProcessor = get { parametersOf(context) }
        )
    }
}

/**
 * Android-specific Koin configuration.
 * 
 * SOLID: Single Responsibility - Only handles Android DI configuration
 */
object AndroidImagePickerKoin {
    
    /**
     * Initializes Koin for Android with context and platform modules.
     * 
     * @param context Android application context
     */
    fun init(context: Context) {
        initImagePickerKoin(
            platformModules = listOf(androidImagePickerModule)
        ) {
            androidLogger(Level.ERROR)
            androidContext(context)
        }
    }
    
}
