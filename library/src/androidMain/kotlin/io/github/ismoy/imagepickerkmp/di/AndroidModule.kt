
package io.github.ismoy.imagepickerkmp.di

import android.content.Context
import androidx.activity.ComponentActivity
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Android-specific Koin module for platform dependencies.
 * 
 * SOLID: Dependency Inversion - Provides concrete implementations for Android
 * SOLID: Single Responsibility - Only handles Android dependency configuration
 */
val imagePickerAndroidModule = module {
    
    // File Manager
    factory { (context: Context) -> 
        FileManager(context) 
    }
    
    // Image Processing
    factory { ImageOrientationCorrector() }
    
    factory { (context: Context) -> 
        ImageProcessor(
            fileManager = get { parametersOf(context) },
            orientationCorrector = get()
        )
    }
    
    // Camera Components
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
