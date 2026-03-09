package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig

/**
 * A composable that provides a complete image picker experience combining camera capture
 * and gallery selection, fully configured through a single [ImagePickerConfig] object.
 *
 * This composable renders no UI by itself — it acts as an invisible launcher that presents
 * the native platform camera or gallery picker according to the provided configuration.
 * Place it inside your composable tree and it launches automatically on the first composition.
 *
 * **Supported platforms:** Android, iOS, Desktop (JVM), Web (JS / Wasm)
 *
 * `ImagePickerLauncher` is the all-in-one alternative to [GalleryPickerLauncher] when you
 * need camera + gallery integration in a single component.
 *
 * ## Basic usage — Camera + Gallery picker dialog
 * ```kotlin
 * ImagePickerLauncher(
 *     config = ImagePickerConfig(
 *         onPhotoCaptured = { photo ->
 *             println("Captured: \${photo.uri}")
 *         },
 *         onError = { error ->
 *             println("Error: \${error.message}")
 *         }
 *     )
 * )
 * ```
 *
 * ## With compression and EXIF
 * ```kotlin
 * ImagePickerLauncher(
 *     config = ImagePickerConfig(
 *         cameraCaptureConfig = CameraCaptureConfig(
 *             compressionLevel = CompressionLevel.HIGH,
 *             includeExif = true
 *         ),
 *         onPhotoCaptured = { photo ->
 *             println("EXIF: \${photo.exif?.cameraMake}")
 *         },
 *         onError = { error -> }
 *     )
 * )
 * ```
 *
 * ## With crop enabled
 * ```kotlin
 * ImagePickerLauncher(
 *     config = ImagePickerConfig(
 *         enableCrop = true,
 *         onPhotoCaptured = { photo -> },
 *         onError = { error -> }
 *     )
 * )
 * ```
 *
 * ## Custom permission dialogs
 * ```kotlin
 * ImagePickerLauncher(
 *     config = ImagePickerConfig(
 *         cameraCaptureConfig = CameraCaptureConfig(
 *             permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
 *                 customDeniedDialog = { onRetry ->
 *                     MyPermissionDeniedDialog(onRetry = onRetry)
 *                 }
 *             )
 *         ),
 *         onPhotoCaptured = { photo -> },
 *         onError = { error -> }
 *     )
 * )
 * ```
 *
 * @param config A [ImagePickerConfig] instance that fully configures the picker behavior,
 *   including callbacks, UI text, camera settings, crop, compression, EXIF extraction,
 *   permission dialogs, and optional custom composable overrides.
 *
 * @see ImagePickerConfig
 * @see io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
 * @see GalleryPickerLauncher
 */
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
