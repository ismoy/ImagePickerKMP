package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.SELECTION_LIMIT
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * ⚠️ **DEPRECATED — Migrate to the new `rememberImagePickerKMP` API.**
 *
 * This composable belongs to the **legacy API (v1)**. It still works but will **not receive
 * new features** and will be removed in a future release. Migrating to the new
 * reactive-state-based API is strongly recommended.
 *
 * ---
 *
 * ## Why migrate?
 *
 * The old API exposed the gallery as a composable with multiple loose parameters
 * (`onPhotosSelected`, `onError`, `onDismiss`, `allowMultiple`, etc.). This caused:
 *
 * - **Hard to reuse**: every screen had to repeat all parameters.
 * - **No observable state**: there was no state object to read the result reactively.
 * - **Composable coupled to the UI tree**: you had to place it even though it rendered nothing.
 * - **Unpredictable behavior on iOS**: callback lifecycle caused crashes when cancelling
 *   the crop inside the native iOS Dialog composition scope.
 *
 * ---
 *
 * ## New API — `rememberImagePickerKMP` (recommended)
 *
 * ### Minimal working implementation
 *
 * This is **everything you need** for a fully functional picker with camera and gallery.
 * No additional configuration is required:
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
 * > `rememberImagePickerKMP()` with no arguments works fully on all platforms out of the box.
 *
 * ---
 *
 * ### Optional: configure multiple selection and filters
 *
 * ```kotlin
 * val picker = rememberImagePickerKMP(
 *     config = ImagePickerKMPConfig(
 *         galleryConfig = GalleryConfig(
 *             allowMultiple  = true,
 *             selectionLimit = 10,
 *             mimeTypes      = listOf(MimeType.IMAGE_JPEG, MimeType.IMAGE_PNG),
 *             includeExif    = true
 *         )
 *     )
 * )
 * picker.launchGallery()
 * ```
 *
 * ### Per-launch overrides
 *
 * Pass parameters directly to `launchGallery()` to override the global config only for
 * that specific call:
 *
 * ```kotlin
 * // Allow multiple selection only for this launch
 * picker.launchGallery(allowMultiple = true, selectionLimit = 5)
 *
 * // Filter by JPEG only for this launch
 * picker.launchGallery(mimeTypes = listOf(MimeType.IMAGE_JPEG))
 * ```
 *
 * ---
 *
 * ## Migration reference (old API → new API)
 *
 * | Old parameter | New API equivalent |
 * |---|---|
 * | `onPhotosSelected = { photos -> }` | `picker.result is ImagePickerResult.Success → result.photos` |
 * | `onError = { e -> }` | `picker.result is ImagePickerResult.Error` |
 * | `onDismiss = { }` | `picker.result is ImagePickerResult.Dismissed` |
 * | `allowMultiple = true` | `GalleryConfig(allowMultiple = true)` or `launchGallery(allowMultiple = true)` |
 * | `selectionLimit = 5` | `GalleryConfig(selectionLimit = 5)` or `launchGallery(selectionLimit = 5)` |
 * | `mimeTypes = listOf(...)` | `GalleryConfig(mimeTypes = ...)` or `launchGallery(mimeTypes = ...)` |
 * | `includeExif = true` | `GalleryConfig(includeExif = true)` or `launchGallery(includeExif = true)` |
 * | `enableCrop = true` | `ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))` |
 * | `cameraCaptureConfig = ...` | `launchGallery(cameraCaptureConfig = ...)` |
 *
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
 * @see io.github.ismoy.imagepickerkmp.domain.config.GalleryConfig
 */
@Deprecated(
    message = """
        GalleryPickerLauncher belongs to the legacy API (v1) and will be removed in a future release.
        
        DELETE the entire GalleryPickerLauncher { ... } block and replace it with:
        
            val picker = rememberImagePickerKMP(
                config = ImagePickerKMPConfig(
                    cropConfig    = CropConfig(enabled = true),
                    galleryConfig = GalleryConfig(
                        allowMultiple  = false,
                        selectionLimit = 1,
                        mimeTypes      = listOf(MimeType.IMAGE_ALL),
                        includeExif    = true
                    )
                )
            )
            val result = picker.result
        
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { picker.launchCamera() },  modifier = Modifier.weight(1f)) { Text("Camera")  }
                Button(onClick = { picker.launchGallery() }, modifier = Modifier.weight(1f)) { Text("Gallery") }
            }
        
            when (result) {
                is ImagePickerResult.Loading   -> CircularProgressIndicator()
                is ImagePickerResult.Success   -> { val photos = result.photos; /* use photos */ }
                is ImagePickerResult.Error     -> Text("Error: ${'$'}{result.exception.message}", color = Color.Red)
                is ImagePickerResult.Dismissed -> Text("Selection cancelled", color = Color.Gray)
                is ImagePickerResult.Idle      -> Text("Press a button to get started", color = Color.Gray)
            }
        
        Per-launch override (no global config change needed):
            picker.launchGallery(allowMultiple = true, selectionLimit = 5)
            picker.launchGallery(mimeTypes = listOf(MimeType.IMAGE_JPEG))
    """,
    replaceWith = ReplaceWith(
        expression = "rememberImagePickerKMP()",
        imports = ["io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP"]
    ),
    level = DeprecationLevel.WARNING
)
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
    mimeTypeMismatchMessage: String? = null,
    onCropPending: () -> Unit = {}
)