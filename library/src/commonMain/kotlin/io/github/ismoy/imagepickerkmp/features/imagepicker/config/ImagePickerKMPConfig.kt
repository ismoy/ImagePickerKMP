package io.github.ismoy.imagepickerkmp.features.imagepicker.config

import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.CropConfig
import io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.domain.config.PermissionAndConfirmationConfig
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig

/**
 * Configuración global para [rememberImagePickerKMP][io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP].
 *
 * ## Dónde va cada opción
 *
 * | Tipo de opción | Dónde configurar |
 * |---|---|
 * | Diálogos personalizados (`@Composable`) | [permissionAndConfirmationConfig] en este config |
 * | Colores / iconos de la UI de la cámara | [uiConfig] en este config |
 * | Comportamiento de cámara/galería por pantalla | parámetros de `launch*()` |
 * | Config fina de cámara (compresión, EXIF, etc.) | [cameraCaptureConfig] en este config |
 *
 * ## Ejemplo completo
 * ```kotlin
 * val picker = rememberImagePickerKMP(
 *     config = ImagePickerKMPConfig(
 *         uiConfig = UiConfig(buttonColor = MaterialTheme.colorScheme.primary),
 *         permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
 *             skipConfirmation = true
 *         ),
 *         cameraCaptureConfig = CameraCaptureConfig(
 *             compressionLevel = CompressionLevel.HIGH,
 *             includeExif = true
 *         ),
 *         galleryConfig = GalleryConfig(allowMultiple = true, selectionLimit = 10),
 *         cropConfig = CropConfig(enabled = true, circularCrop = false, squareCrop = true)
 *     )
 * )
 * ```
 *
 * @property cameraCaptureConfig Comportamiento de la cámara (compresión, EXIF, tamaño botón, etc.).
 * @property galleryConfig Comportamiento de la galería (selección múltiple, MIME types, etc.).
 * @property cropConfig Activar y ajustar el crop: usar `CropConfig(enabled = true)` para mostrar
 *   la UI de recorte tras cada captura/selección. Soporta circular, cuadrado, freeform y aspect ratio.
 * @property uiConfig Colores e iconos personalizados para la UI de la cámara.
 * @property permissionAndConfirmationConfig Diálogos `@Composable` personalizados para permisos
 *   y pantalla de confirmación.
 */
data class ImagePickerKMPConfig(
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val galleryConfig: GalleryConfig = GalleryConfig(),
    val cropConfig: CropConfig = CropConfig(),
    val uiConfig: UiConfig = UiConfig(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig()
)
