package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler

/**
 * ImagePickerLauncher
 * @param context Contexto de Android
 * @param onPhotoCaptured Callback para foto tomada con la cámara
 * @param onPhotosSelected Callback para selección múltiple en galería
 * @param onError Callback para errores generales
 * @param customPermissionHandler Permite personalizar el flujo de permisos
 * @param customConfirmationView Permite personalizar la UI de confirmación
 * @param preference Preferencia de calidad de la foto
 * @param dialogTitle Título del diálogo de selección
 * @param takePhotoText Texto para la opción de tomar foto
 * @param selectFromGalleryText Texto para la opción de galería
 * @param cancelText Texto para cancelar
 * @param buttonColor Color de los botones principales
 * @param iconColor Color de los iconos
 * @param buttonSize Tamaño de los botones
 * @param layoutPosition Posición de los botones principales ("bottom", "top", etc)
 * @param flashIcon Icono personalizado para flash
 * @param switchCameraIcon Icono personalizado para cambiar cámara
 * @param captureIcon Icono personalizado para capturar
 * @param galleryIcon Icono personalizado para galería
 * @param onCameraReady Callback cuando la cámara está lista
 * @param onCameraSwitch Callback cuando se cambia de cámara
 * @param onPermissionError Callback para error de permisos
 * @param onGalleryOpened Callback cuando se abre la galería
 * @param allowMultiple Permite selección múltiple en galería
 * @param mimeTypes Tipos MIME permitidos en galería
 */
@Composable
expect fun ImagePickerLauncher(
    context: Any?,
    onPhotoCaptured: (PhotoResult) -> Unit,
    onPhotosSelected: ((List<GalleryPhotoHandler.PhotoResult>) -> Unit)? = null,
    onError: (Exception) -> Unit,
    customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    preference: CapturePhotoPreference? = null,
    dialogTitle: String = "Select option",
    takePhotoText: String = "Take photo",
    selectFromGalleryText: String = "Select from gallery",
    cancelText: String = "Cancel",
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
)