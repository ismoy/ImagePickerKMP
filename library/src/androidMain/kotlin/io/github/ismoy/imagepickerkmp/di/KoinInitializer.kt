package io.github.ismoy.imagepickerkmp.di

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.camera.view.PreviewView
import io.github.ismoy.imagepickerkmp.data.camera.CameraController
import io.github.ismoy.imagepickerkmp.data.camera.CameraXManager
import io.github.ismoy.imagepickerkmp.data.managers.FileManager
import io.github.ismoy.imagepickerkmp.data.processors.ImageOrientationCorrector
import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.presentation.ui.components.CameraCaptureStateHolder
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
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
        CameraController(
            context = context, 
            lifecycleOwner = activity,
            fileManager = get { parametersOf(context) }
        ) 
    }
    
    factory { (context: Context, activity: ComponentActivity) -> 
        CameraXManager(
            cameraController = get { parametersOf(context, activity) },
            imageProcessor = get { parametersOf(context) }
        )
    }
    
    factory { (previewView: PreviewView, preference: CapturePhotoPreference, scope: CoroutineScope) ->
        val context = previewView.context
        val activity = context as ComponentActivity
        val cameraManager: CameraXManager = get { parametersOf(context, activity) }
        CameraCaptureStateHolder(
            cameraManager = cameraManager,
            previewView = previewView,
            preference = preference,
            coroutineScope = scope
        )
    }
}

/**
 * Android-specific Koin initialization.
 * Configures Koin with common modules and Android-specific modules.
 */
actual fun initKoin(configuration: KoinApplication.() -> Unit) {
    startKoin {
        configuration(this)
        androidLogger(Level.ERROR)
        modules(imagePickerCommonModule, androidImagePickerModule)
    }
}
