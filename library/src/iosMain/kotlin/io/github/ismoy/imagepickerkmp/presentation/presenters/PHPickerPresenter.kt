package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.PHPickerDelegate
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerViewController
import platform.Photos.PHPhotoLibrary
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.UIKit.UIViewController
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Presents the PHPickerViewController for multiple photo selection on iOS 14+.
 *
 * This object manages the presentation and delegation of the PHPickerViewController.
 */
object PHPickerPresenter {
    private var pickerDelegate: PHPickerDelegate? = null

    fun presentGallery(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
    ) {
        require(selectionLimit <= SELECTION_LIMIT) {"Selection limit cannot exceed $SELECTION_LIMIT"}
        
        // Si necesitamos EXIF, solicitar permisos primero
        if (includeExif) {
            println("📸 PHPickerPresenter: EXIF requested, checking photo library permissions")
            checkPhotoLibraryPermission(
                viewController = viewController,
                onAuthorized = {
                    println("✅ PHPickerPresenter: Photo library access granted")
                    presentPickerViewController(
                        viewController,
                        onPhotoSelected,
                        onError,
                        onDismiss,
                        selectionLimit,
                        compressionLevel,
                        includeExif,
                        onPhotosSelected
                    )
                },
                onDenied = {
                    println("❌ PHPickerPresenter: Photo library access denied, showing dialog")
                    showPermissionDeniedDialog(viewController, onDismiss)
                }
            )
        } else {
            // Si no necesitamos EXIF, mostrar el picker directamente
            println("📸 PHPickerPresenter: EXIF not requested, presenting picker directly")
            presentPickerViewController(
                viewController,
                onPhotoSelected,
                onError,
                onDismiss,
                selectionLimit,
                compressionLevel,
                includeExif,
                onPhotosSelected
            )
        }
    }
    
    private fun checkPhotoLibraryPermission(
        viewController: UIViewController,
        onAuthorized: () -> Unit,
        onDenied: () -> Unit
    ) {
        val currentStatus = PHPhotoLibrary.authorizationStatus()
        println("📋 Current authorization status: $currentStatus")
        
        when (currentStatus) {
            PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> {
                println("✅ Already authorized")
                onAuthorized()
            }
            PHAuthorizationStatusNotDetermined -> {
                println("❓ Permission not determined, requesting...")
                PHPhotoLibrary.requestAuthorization { status ->
                    println("📋 Permission result: $status")
                    // ⚠️ CRITICAL: El callback se ejecuta en un hilo en segundo plano
                    // Debemos volver al hilo principal antes de llamar a onAuthorized/onDenied
                    dispatch_async(dispatch_get_main_queue()) {
                        when (status) {
                            PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited -> {
                                onAuthorized()
                            }
                            else -> {
                                onDenied()
                            }
                        }
                    }
                }
            }
            PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> {
                println("❌ Access denied or restricted")
                onDenied()
            }
            else -> {
                println("⚠️ Unknown authorization status")
                onDenied()
            }
        }
    }
    
    private fun presentPickerViewController(
        viewController: UIViewController,
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
    ) {
        try {
            val pickerViewController = createPHPickerController(
                onPhotoSelected,
                onError,
                onDismiss,
                selectionLimit,
                compressionLevel,
                includeExif,
                onPhotosSelected
            )
            viewController.presentViewController(pickerViewController,
                animated = true, completion = null)
        } catch (e: Exception) {
            onError(Exception("Failed to present PHPicker: ${e.message}"))
        }
    }

    private fun createPHPickerController(
        onPhotoSelected: (GalleryPhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        onDismiss: () -> Unit,
        selectionLimit: Long,
        compressionLevel: CompressionLevel? = null,
        includeExif: Boolean = false,
        onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null
    ): PHPickerViewController {
        val configuration = PHPickerConfiguration(
            photoLibrary = platform.Photos.PHPhotoLibrary.sharedPhotoLibrary()
        )
        configuration.selectionLimit = selectionLimit
        configuration.filter = PHPickerFilter.imagesFilter

        val cleanup = { pickerDelegate = null }
        val wrappedOnPhotoSelected: (GalleryPhotoResult) -> Unit = { result ->
            onPhotoSelected(result)
            cleanup()
        }
        val wrappedOnError: (Exception) -> Unit = { error ->
            onError(error)
            cleanup()
        }
        val wrappedOnDismiss: () -> Unit = {
            onDismiss()
            cleanup()
        }
        pickerDelegate = PHPickerDelegate(
            wrappedOnPhotoSelected,
            onPhotosSelected,
            wrappedOnError,
            wrappedOnDismiss,
            compressionLevel,
            includeExif
        )
        return PHPickerViewController(configuration).apply {
            delegate = pickerDelegate
        }
    }
    
    /**
     * Muestra un diálogo nativo de iOS cuando los permisos de la biblioteca de fotos son denegados.
     * El diálogo ofrece al usuario la opción de abrir Configuración para habilitar los permisos.
     */
    fun showPermissionDeniedDialog(
        viewController: UIViewController,
        onDismiss: () -> Unit
    ) {
        dispatch_async(dispatch_get_main_queue()) {
            val alert = UIAlertController.alertControllerWithTitle(
                title = "Photo Library Access Required",
                message = "To extract photo metadata (location, date, camera info), we need access to your Photo Library. Please enable it in Settings.",
                preferredStyle = UIAlertControllerStyleAlert
            )
            
            // Botón "Open Settings"
            val settingsAction = UIAlertAction.actionWithTitle(
                title = "Open Settings",
                style = UIAlertActionStyleDefault,
                handler = {
                    val settingsUrl = NSURL.URLWithString("app-settings:")
                    if (settingsUrl != null) {
                        UIApplication.sharedApplication.openURL(
                            settingsUrl,
                            options = emptyMap<Any?, Any>(),
                            completionHandler = { success ->
                                println("✅ Settings opened: $success")
                            }
                        )
                    }
                    onDismiss()
                }
            )
            
            // Botón "Cancel"
            val cancelAction = UIAlertAction.actionWithTitle(
                title = "Cancel",
                style = UIAlertActionStyleCancel,
                handler = {
                    onDismiss()
                }
            )
            
            alert.addAction(settingsAction)
            alert.addAction(cancelAction)
            
            viewController.presentViewController(alert, animated = true, completion = null)
        }
    }
}



