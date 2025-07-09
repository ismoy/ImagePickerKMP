package io.github.ismoy.imagepickerkmp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.Constant.PERMISSION_PERMANENTLY_DENIED
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

/**
 * Entry point de alto nivel para el flujo de captura de imagen.
 *
 * Este Composable gestiona:
 * - Permisos de cámara (mostrando diálogos personalizados si es necesario)
 * - Renderizado de la UI de cámara ([CameraCapturePreview])
 * - Renderizado de la pantalla de confirmación ([ImageConfirmationViewWithCustomButtons])
 * - Integración con selección múltiple desde galería
 *
 * ## Uso típico:
 *
 * ```kotlin
 * CameraCaptureView(
 *     activity = activity,
 *     onPhotoResult = { ... },
 *     onError = { ... }
 * )
 * ```
 *
 * ## Parámetros clave:
 * - [activity]: Activity host (necesario para CameraX y permisos)
 * - [onPhotoResult]: Callback con el resultado de la foto
 * - [onPhotosSelected]: Callback para selección múltiple desde galería
 * - [onError]: Callback para errores
 * - [customPermissionHandler]: Permite personalizar los diálogos de permisos
 * - [customConfirmationView]: Permite personalizar la pantalla de confirmación
 * - Personalización de colores, iconos, layout y eventos
 *
 * ## Flujo general:
 * 1. Solicita permisos de cámara
 * 2. Si se otorgan, muestra la UI de cámara
 * 3. Tras capturar, muestra la pantalla de confirmación
 * 4. Permite seleccionar desde galería si está habilitado
 *
 * @see CameraCapturePreview
 * @see ImageConfirmationViewWithCustomButtons
 */
@Composable
fun CameraCaptureView(
    activity: ComponentActivity,
    preference: CapturePhotoPreference = CapturePhotoPreference.FAST,
    onPhotoResult: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
    onCameraReady: (() -> Unit)? = null,
    onCameraSwitch: (() -> Unit)? = null,
    onPermissionError: ((Exception) -> Unit)? = null,
    onGalleryOpened: (() -> Unit)? = null,
    allowMultiple: Boolean = false,
    mimeTypes: List<String> = listOf("image/*")
){
    val context = LocalContext.current
    var photoResult by remember { mutableStateOf<PhotoResult?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    val cameraManager = remember { CameraXManager(context, activity) }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopCamera()
        }
    }

    if (!hasPermission){
        val defaultConfig = PermissionConfig()
        if (customPermissionHandler != null){
            customPermissionHandler(defaultConfig)
        }
        else {
            RequestCameraPermission(
                titleDialogConfig = defaultConfig.titleDialogConfig,
                descriptionDialogConfig = defaultConfig.descriptionDialogConfig,
                btnDialogConfig = defaultConfig.btnDialogConfig,
                titleDialogDenied = defaultConfig.titleDialogDenied,
                descriptionDialogDenied = defaultConfig.descriptionDialogDenied,
                btnDialogDenied = defaultConfig.btnDialogDenied,
                customDeniedDialog = null,
                customSettingsDialog = null,
                onPermissionPermanentlyDenied = {
                    onError(PhotoCaptureException(PERMISSION_PERMANENTLY_DENIED))
                },
                onResult = {granted->
                    hasPermission = granted
                }
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (allowMultiple && onPhotosSelected != null) {
            GalleryPickerLauncher(
                context = activity,
                onPhotosSelected = { results -> onPhotosSelected(results) },
                onError = onError,
                allowMultiple = true,
                mimeTypes = mimeTypes
            )
        } else if (photoResult == null) {
            CameraCapturePreview(
                cameraManager = cameraManager,
                preference = preference,
                onPhotoResult = { result ->
                    photoResult = result
                    playShutterSound()
                },
                context = context,
                onError = onError,
                buttonColor = buttonColor,
                iconColor = iconColor,
                buttonSize = buttonSize,
                layoutPosition = layoutPosition,
                flashIcon = flashIcon,
                switchCameraIcon = switchCameraIcon,
                captureIcon = captureIcon,
                galleryIcon = galleryIcon,
                onCameraReady = onCameraReady,
                onCameraSwitch = onCameraSwitch,
                onPermissionError = onPermissionError,
                onGalleryOpened = onGalleryOpened
            )
        } else {
            ImageConfirmationViewWithCustomButtons(
                result = photoResult!!,
                onConfirm = { onPhotoResult(it) },
                onRetry = { photoResult = null },
                customConfirmationView = customConfirmationView,
                buttonColor = buttonColor,
                iconColor = iconColor,
                buttonSize = buttonSize,
                layoutPosition = layoutPosition,
                flashIcon = flashIcon,
                switchCameraIcon = switchCameraIcon,
                captureIcon = captureIcon,
                galleryIcon = galleryIcon
            )
        }
    }
}