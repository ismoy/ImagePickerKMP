package io.github.ismoy.imagepickerkmp.domain.config

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult

/**
 * Configuration for the visual styling of camera UI elements.
 *
 * @property buttonColor Optional color override for primary action buttons (e.g., capture button).
 *   Defaults to `null` (uses theme defaults).
 * @property iconColor Optional color override for icons inside the camera UI. Defaults to `null`.
 * @property buttonSize Optional size override for the primary capture button in [Dp].
 *   Defaults to `null` (falls back to [CameraCaptureConfig.captureButtonSize]).
 * @property flashIcon Optional custom [ImageVector] to replace the default flash toggle icon.
 * @property switchCameraIcon Optional custom [ImageVector] to replace the default
 *   front/back camera switch icon.
 * @property galleryIcon Optional custom [ImageVector] to replace the default gallery access icon.
 */
@Suppress("EndOfSentenceFormat")
data class UiConfig(
    val buttonColor: Color? = null,
    val iconColor: Color? = null,
    val buttonSize: Dp? = null,
    val flashIcon: ImageVector? = null,
    val switchCameraIcon: ImageVector? = null,
    val galleryIcon: ImageVector? = null
)

/**
 * Lifecycle callbacks for camera events.
 *
 * @property onCameraReady Called when the camera preview is fully initialized and ready
 *   to capture. Use this to show/hide loading indicators. Defaults to `null`.
 * @property onCameraSwitch Called after the camera switches between front and back.
 *   Defaults to `null`.
 * @property onPermissionError Called when the camera permission is denied or unavailable.
 *   The [Exception] contains the reason. Defaults to `null`.
 * @property onGalleryOpened Called when the user navigates from the camera to the gallery
 *   picker. Defaults to `null`.
 */
@Suppress("EndOfSentenceFormat")
data class CameraCallbacks(
    val onCameraReady: (() -> Unit)? = null,
    val onCameraSwitch: (() -> Unit)? = null,
    val onPermissionError: ((Exception) -> Unit)? = null,
    val onGalleryOpened: (() -> Unit)? = null
)

/**
 * Configuration for permission request dialogs and post-capture confirmation screen.
 *
 * @property customPermissionHandler Optional callback invoked instead of showing the default
 *   permission request dialog. Receives the current [PermissionConfig] so you can read
 *   localized strings. Defaults to `null`.
 * @property customConfirmationView Optional composable that replaces the built-in photo
 *   confirmation screen. Receives the captured [PhotoResult], a confirmation callback, and
 *   a retry callback. Defaults to `null`.
 * @property customDeniedDialog Optional composable shown when camera permission is denied.
 *   Receives `onRetry` to re-trigger the permission request and `onDismiss` to close the
 *   entire picker without action. Always call one of them when the user dismisses the dialog.
 *   Defaults to `null`.
 * @property customSettingsDialog Optional composable shown when the user must open system
 *   settings to grant permission. Receives `onOpenSettings` to open system settings and
 *   `onDismiss` to close the picker without action. Always call one of them on dismiss.
 *   Defaults to `null`.
 * @property skipConfirmation When `true`, skips the confirmation screen entirely and delivers
 *   the captured photo directly via `onPhotoCaptured`. Defaults to `false`.
 * @property cancelButtonTextIOS Text label for the cancel button in the iOS permission
 *   configuration alert. Defaults to `"Cancel"`. Has no effect on Android.
 * @property onCancelPermissionConfigIOS Callback invoked on iOS when the user taps the cancel
 *   button in the permission configuration alert. Defaults to `null`.
 */
@Suppress("EndOfSentenceFormat", "MaxLineLength")
data class PermissionAndConfirmationConfig(
    val customPermissionHandler: ((PermissionConfig) -> Unit)? = null,
    val customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    val customDeniedDialog: (@Composable (onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: (@Composable (onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val skipConfirmation: Boolean = false,
    val cancelButtonTextIOS: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)

/**
 * Configuration for the gallery selection portion of the picker.
 *
 * @property allowMultiple Whether to allow selecting multiple files simultaneously.
 *   Defaults to `false`.
 * @property mimeTypes List of [MimeType] values that restrict selectable files.
 *   Defaults to [MimeType.IMAGE_ALL] (accepts all image formats).
 * @property selectionLimit Maximum number of selectable files when [allowMultiple] is `true`.
 *   Defaults to `30`.
 * @property includeExif Whether to extract EXIF metadata from selected images.
 *   Defaults to `false`.
 * @property redactGpsData When `true` (the default), GPS latitude, longitude, and altitude fields
 *   are stripped from extracted EXIF data before delivery.  Set to `false` only when your app
 *   has a legitimate need for the exact capture location and the user has been informed.
 *   Effective only when [includeExif] is `true`.
 * @property mimeTypeMismatchMessage Custom message shown when a selected file does not match
 *   the allowed [mimeTypes]. Defaults to `null` (uses built-in message).
 */
@Suppress("EndOfSentenceFormat")
data class GalleryConfig(
    val allowMultiple: Boolean = false,
    val mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    val selectionLimit: Int = 30,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val mimeTypeMismatchMessage: String? = null
)

/**
 * Configuration for the interactive image crop UI.
 *
 * When [enabled] is `true`, the user is presented with a crop overlay after selecting
 * or capturing an image.
 *
 * @property enabled Whether to show the crop UI. Defaults to `false`.
 * @property aspectRatioLocked Whether the crop rectangle maintains a fixed aspect ratio.
 *   Defaults to `false` (free-form unless restricted by other flags).
 * @property circularCrop Whether to offer a circular crop option. Defaults to `true`.
 * @property squareCrop Whether to offer a square (1:1) crop option. Defaults to `true`.
 * @property freeformCrop Whether to allow freeform (unconstrained) cropping. Defaults to `false`.
 */
@Suppress("EndOfSentenceFormat")
data class CropConfig(
    val enabled: Boolean = false,
    val aspectRatioLocked: Boolean = false,
    val circularCrop: Boolean = true,
    val squareCrop: Boolean = true,
    val freeformCrop: Boolean = false
)

/**
 * Configuration for the embedded camera capture feature.
 *
 * Used by both [io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher] and [io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher] to control camera behavior,
 * compression, UI styling, permission handling, and crop integration.
 *
 * @property preference Capture quality preference that balances speed, quality, and
 *   memory usage. Defaults to [CapturePhotoPreference.BALANCED].
 * @property captureButtonSize Size of the shutter/capture button in [Dp]. Defaults to `72.dp`.
 * @property compressionLevel Optional [CompressionLevel] applied to the captured image before
 *   delivery. `null` means no compression (full quality). Defaults to [CompressionLevel.MEDIUM].
 * @property includeExif Whether to extract and attach EXIF metadata to the [PhotoResult].
 *   On iOS, this requires photo library permission. Defaults to `false`.
 * @property redactGpsData When `true` (the default), GPS latitude, longitude, and altitude
 *   are stripped from the EXIF data before delivery. Set to `false` only when your app has a
 *   legitimate need for the capture location and the user has been informed.
 *   Effective only when [includeExif] is `true`.
 * @property uiConfig Visual styling overrides for camera UI elements. See [UiConfig].
 * @property cameraCallbacks Lifecycle event callbacks for camera events. See [CameraCallbacks].
 * @property permissionAndConfirmationConfig Configuration for permission dialogs and the
 *   post-capture confirmation screen. See [PermissionAndConfirmationConfig].
 * @property cropConfig Configuration for the interactive crop UI shown after capture.
 *   See [CropConfig].
 */
@Suppress("EndOfSentenceFormat")
data class CameraCaptureConfig(
    val preference: CapturePhotoPreference = CapturePhotoPreference.BALANCED,
    val captureButtonSize: Dp = 72.dp,
    val compressionLevel: CompressionLevel? = CompressionLevel.MEDIUM,
    val includeExif: Boolean = false,
    val redactGpsData: Boolean = true,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks(),
    val permissionAndConfirmationConfig: PermissionAndConfirmationConfig = PermissionAndConfirmationConfig(),
    val cropConfig: CropConfig = CropConfig()
)

/**
 * ⚠️ **DEPRECATED — Belongs to the legacy API (v1).**
 *
 * `ImagePickerConfig` was the central configuration object for [ImagePickerLauncher],
 * the old-API composable. Both (`ImagePickerConfig` + `ImagePickerLauncher`) will be
 * removed in a future release.
 *
 * ## Migrating to the new API
 *
 * In the new API you **do not construct an `ImagePickerConfig` manually**. Instead:
 *
 * 1. Call `rememberImagePickerKMP()` inside your composable — it returns an
 *    [ImagePickerKMPState][io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState].
 * 2. Call `picker.launchCamera()` or `picker.launchGallery()` on user interaction.
 * 3. Observe `picker.result` reactively with a `when` expression.
 *
 * ### Minimal working implementation
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val picker = rememberImagePickerKMP()
 *     val result = picker.result
 *
 *     Row(
 *         modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
 *         horizontalArrangement = Arrangement.spacedBy(8.dp)
 *     ) {
 *         Button(onClick = { picker.launchCamera() }, modifier = Modifier.weight(1f)) {
 *             Text("Camera")
 *         }
 *         Button(onClick = { picker.launchGallery() }, modifier = Modifier.weight(1f)) {
 *             Text("Gallery")
 *         }
 *     }
 *
 *     when (result) {
 *         is ImagePickerResult.Loading -> {
 *             Column(
 *                 horizontalAlignment = Alignment.CenterHorizontally,
 *                 modifier = Modifier.padding(16.dp)
 *             ) {
 *                 CircularProgressIndicator()
 *                 Text("Loading...", color = Color.Gray, modifier = Modifier.padding(top = 12.dp))
 *             }
 *         }
 *         is ImagePickerResult.Success -> {
 *             val photos = result.photos
 *             if (photos.size == 1) {
 *                 CameraResultCard(photo = photos.first())
 *             } else {
 *                 MultiPhotoGrid(photos = photos)
 *             }
 *         }
 *         is ImagePickerResult.Error     -> Text("Error: \${result.exception.message}", color = Color.Red)
 *         is ImagePickerResult.Dismissed -> Text("Selection cancelled", color = Color.Gray)
 *         is ImagePickerResult.Idle      -> Text("Press a button to get started", color = Color.Gray)
 *     }
 * }
 * ```
 *
 * > ✅ **That's all you need for a basic implementation.**
 *
 * Configuration that previously went into [ImagePickerConfig] now goes into
 * [ImagePickerKMPConfig][io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig]:
 *
 * ```kotlin
 * val picker = rememberImagePickerKMP(
 *     config = ImagePickerKMPConfig(
 *         cropConfig          = CropConfig(enabled = true),
 *         cameraCaptureConfig = CameraCaptureConfig(compressionLevel = CompressionLevel.HIGH)
 *     )
 * )
 * ```
 *
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
 */
data class ImagePickerConfig(
    val onPhotoCaptured: (PhotoResult) -> Unit,
    val onError: (Exception) -> Unit,
    val onDismiss: () -> Unit = {},
    val cameraCaptureConfig: CameraCaptureConfig = CameraCaptureConfig(),
    val enableCrop: Boolean = false,
    /**
     * Llamado justo antes de mostrar el crop view, después de capturar/seleccionar la foto.
     * Úsalo para cambiar el estado de tu UI (p.ej. ocultar un spinner de "Loading").
     */
    val onCropPending: () -> Unit = {}
)

data class CameraPreviewConfig(
    val captureButtonSize: Dp = 72.dp,
    val uiConfig: UiConfig = UiConfig(),
    val cameraCallbacks: CameraCallbacks = CameraCallbacks()
)

/**
 * Configuration for camera permission dialogs.
 * 
 * @param titleDialogConfig Title text for the permission configuration dialog
 * @param descriptionDialogConfig Description text for the permission configuration dialog
 * @param btnDialogConfig Button text for the permission configuration dialog
 * @param titleDialogDenied Title text when permission is denied
 * @param descriptionDialogDenied Description text when permission is denied
 * @param btnDialogDenied Button text when permission is denied
 * @param customDeniedDialog Custom composable dialog when permission is denied
 * @param customSettingsDialog Custom composable dialog for opening settings
 * @param cancelButtonText Text for the cancel button (iOS only, default: "Cancel")
 * @param onCancelPermissionConfigIOS Callback when user cancels permission config dialog (iOS only)
 */
data class CameraPermissionDialogConfig(
    val titleDialogConfig: String,
    val descriptionDialogConfig: String,
    val btnDialogConfig: String,
    val titleDialogDenied: String,
    val descriptionDialogDenied: String,
    val btnDialogDenied: String,
    val customDeniedDialog: @Composable ((onRetry: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val customSettingsDialog: @Composable ((onOpenSettings: () -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    val cancelButtonText: String? = "Cancel",
    val onCancelPermissionConfigIOS: (() -> Unit)? = null
)
