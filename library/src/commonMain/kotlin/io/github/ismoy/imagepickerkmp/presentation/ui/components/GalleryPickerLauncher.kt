package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * A composable that provides a gallery image picker with support for single and multiple
 * selection, MIME type filtering, crop, EXIF metadata extraction, and optional camera integration.
 *
 * This composable renders no UI by itself — it acts as an invisible launcher that triggers
 * the native platform gallery picker. Place it inside your composable tree and it will
 * automatically launch the picker on the first composition.
 *
 * **Supported platforms:** Android, iOS, Desktop (JVM), Web (JS / Wasm)
 *
 * ## Basic usage
 * ```kotlin
 * GalleryPickerLauncher(
 *     onPhotosSelected = { photos ->
 *         photos.forEach { photo ->
 *             println("Selected: \${photo.uri}")
 *         }
 *     },
 *     onError = { error ->
 *         println("Error: \${error.message}")
 *     }
 * )
 * ```
 *
 * ## Filtering by MIME type
 * ```kotlin
 * GalleryPickerLauncher(
 *     mimeTypes = listOf(MimeType.IMAGE_PNG, MimeType.IMAGE_WEBP),
 *     mimeTypeMismatchMessage = "Only PNG and WebP images are allowed.",
 *     onPhotosSelected = { photos -> },
 *     onError = { error -> }
 * )
 * ```
 *
 * ## Multiple selection with limit
 * ```kotlin
 * GalleryPickerLauncher(
 *     allowMultiple = true,
 *     selectionLimit = 5,
 *     onPhotosSelected = { photos -> },
 *     onError = { error -> }
 * )
 * ```
 *
 * ## With crop enabled
 * ```kotlin
 * GalleryPickerLauncher(
 *     enableCrop = true,
 *     onPhotosSelected = { photos -> },
 *     onError = { error -> }
 * )
 * ```
 *
 * ## With EXIF metadata
 * ```kotlin
 * GalleryPickerLauncher(
 *     includeExif = true,
 *     onPhotosSelected = { photos ->
 *         photos.firstOrNull()?.exif?.let { exif ->
 *             println("GPS: \${exif.latitude}, \${exif.longitude}")
 *         }
 *     },
 *     onError = { error -> }
 * )
 * ```
 *
 * @param onPhotosSelected Called with the list of [GalleryPhotoResult] when the user confirms
 *   the selection. On single-selection mode ([allowMultiple] = `false`), the list contains
 *   exactly one element.
 * @param onError Called when an error occurs during the picking process. The [Exception]
 *   message describes the failure reason (e.g., permission denied, MIME type mismatch).
 * @param onDismiss Called when the user dismisses the picker without selecting any file.
 *   Defaults to an empty lambda.
 * @param allowMultiple Whether to allow selecting multiple files at once. When `true`,
 *   the picker enables multi-selection up to [selectionLimit] items. Defaults to `false`.
 * @param mimeTypes List of [MimeType] values that restrict which files the user can select.
 *   Files not matching any of the specified types will trigger [onError] with a mismatch
 *   message. Defaults to [MimeType.IMAGE_ALL] (accepts all image formats).
 *   Example: `listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG)`.
 * @param selectionLimit Maximum number of files the user can select when [allowMultiple]
 *   is `true`. Must be between 1 and the platform maximum. Defaults to [SELECTION_LIMIT].
 * @param cameraCaptureConfig Optional [CameraCaptureConfig] to enable an integrated camera
 *   capture option alongside the gallery. When non-null, a camera button or flow is
 *   presented to the user. Defaults to `null` (gallery-only).
 * @param enableCrop Whether to show an interactive crop UI after the user selects an image.
 *   When `true`, the user can freely crop the image before it is delivered via
 *   [onPhotosSelected]. Defaults to `false`.
 *   > **Note:** Crop is only applied to images, not documents (e.g., PDFs).
 * @param fileFilterDescription A human-readable label describing the allowed file types.
 *   Shown in the Desktop (JVM) file chooser dialog filter dropdown.
 *   Defaults to `"Image files"`.
 * @param includeExif Whether to extract and include EXIF metadata (e.g., GPS coordinates,
 *   camera model, date taken) in the [GalleryPhotoResult.exif] field. Requires photo
 *   library permission on iOS. Defaults to `false`.
 * @param mimeTypeMismatchMessage Optional custom error message shown when the selected file
 *   does not match any of the specified [mimeTypes]. When `null`, a default localized
 *   message is used. Defaults to `null`.
 *   Example: `"Only JPEG images are allowed."`.
 *
 * @see GalleryPhotoResult
 * @see MimeType
 * @see CameraCaptureConfig
 */
@Suppress("LongParameterList")
@Composable
expect fun GalleryPickerLauncher(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit = {},
    allowMultiple: Boolean = false,
    mimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL),
    selectionLimit: Long = SELECTION_LIMIT,
    cameraCaptureConfig: CameraCaptureConfig? = null,
    enableCrop: Boolean = false,
    fileFilterDescription: String = "Image files",
    includeExif: Boolean = false,
    mimeTypeMismatchMessage: String? = null
)

