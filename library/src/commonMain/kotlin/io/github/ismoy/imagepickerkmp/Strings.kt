package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable

/**
 * Enum class que define todos los identificadores de strings utilizados en la librería.
 * Esto evita errores con textos hardcodeados y proporciona type safety.
 * 
 * Idiomas soportados:
 * - Inglés (en)
 * - Español (es) 
 * - Francés (fr)
 */
enum class StringResource {
    // Permisos de cámara
    CAMERA_PERMISSION_REQUIRED,
    CAMERA_PERMISSION_DESCRIPTION,
    OPEN_SETTINGS,
    CAMERA_PERMISSION_DENIED,
    CAMERA_PERMISSION_DENIED_DESCRIPTION,
    GRANT_PERMISSION,
    CAMERA_PERMISSION_PERMANENTLY_DENIED,
    
    // Confirmación de imagen
    IMAGE_CONFIRMATION_TITLE,
    ACCEPT_BUTTON,
    RETRY_BUTTON,
    
    // Diálogos de selección
    SELECT_OPTION_DIALOG_TITLE,
    TAKE_PHOTO_OPTION,
    SELECT_FROM_GALLERY_OPTION,
    CANCEL_OPTION,
    
    // Accesibilidad
    PREVIEW_IMAGE_DESCRIPTION,
    HD_QUALITY_DESCRIPTION,
    SD_QUALITY_DESCRIPTION,
    
    // Errores
    INVALID_CONTEXT_ERROR,
    PHOTO_CAPTURE_ERROR,
    GALLERY_SELECTION_ERROR,
    PERMISSION_ERROR
}

/**
 * Función expect que debe ser implementada por cada plataforma.
 * Proporciona acceso a los strings localizados según el idioma del dispositivo.
 */
@Composable
internal expect fun stringResource(id: StringResource): String

/**
 * Función de conveniencia para obtener strings sin Composable.
 * Útil para casos donde no se tiene acceso al contexto de Compose.
 */
internal expect fun getStringResource(id: StringResource): String 