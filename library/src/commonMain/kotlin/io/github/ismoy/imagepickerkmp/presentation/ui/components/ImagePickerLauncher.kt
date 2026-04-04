package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig

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
 * The old API required you to manually construct and inject an [ImagePickerConfig] with
 * loose callbacks (`onPhotoCaptured`, `onError`, `onDismiss`). This caused several issues:
 *
 * - **Tight coupling**: business logic lived inside lambdas injected into the UI layer.
 * - **Hard to observe state**: there was no single place to read the result reactively.
 * - **Composable polluting the tree**: you had to place `ImagePickerLauncher(...)` in your
 *   UI as if it were a widget, even though it rendered nothing visible.
 * - **iOS crash on crop cancel**: the direct-callback architecture caused Compose state
 *   mutations in forbidden phases of the composition lifecycle.
 *
 * ---
 *
 * ## New API — `rememberImagePickerKMP` (recommended)
 *
 * The new API follows the standard Compose pattern: a **state hook** that returns an
 * observable object you can use to launch the picker and read the result anywhere in the tree.
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
 * > Permissions, native UI, and lifecycle are handled automatically on all platforms.
 *
 * ---
 *
 * ### Optional advanced configuration
 *
 * If you need to customize behavior, pass an
 * [ImagePickerKMPConfig][io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig]
 * to `rememberImagePickerKMP`:
 *
 * ```kotlin
 * val picker = rememberImagePickerKMP(
 *     config = ImagePickerKMPConfig(
 *         cropConfig          = CropConfig(enabled = true, squareCrop = true),
 *         galleryConfig       = GalleryConfig(allowMultiple = true, selectionLimit = 10),
 *         cameraCaptureConfig = CameraCaptureConfig(
 *             compressionLevel = CompressionLevel.HIGH,
 *             includeExif      = true
 *         )
 *     )
 * )
 * ```
 *
 * ### Per-launch overrides
 *
 * You can also pass parameters directly to the launch method to override the global
 * config only for that call:
 *
 * ```kotlin
 * // Allow multiple selection only for this launch
 * picker.launchGallery(allowMultiple = true, selectionLimit = 5)
 *
 * // Enable EXIF extraction only for this camera launch
 * picker.launchCamera(cameraCaptureConfig = CameraCaptureConfig(includeExif = true))
 * ```
 *
 * ---
 *
 * ## Migration reference (old API → new API)
 *
 * | Old API | New API equivalent |
 * |---|---|
 * | `ImagePickerLauncher(config = ImagePickerConfig(...))` | `val picker = rememberImagePickerKMP()` |
 * | `onPhotoCaptured = { photo -> }` | `picker.result is ImagePickerResult.Success → result.photos` |
 * | `onError = { e -> }` | `picker.result is ImagePickerResult.Error` |
 * | `onDismiss = { }` | `picker.result is ImagePickerResult.Dismissed` |
 * | `enableCrop = true` | `ImagePickerKMPConfig(cropConfig = CropConfig(enabled = true))` |
 * | `cameraCaptureConfig = CameraCaptureConfig(...)` | `ImagePickerKMPConfig(cameraCaptureConfig = ...)` |
 *
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.state.ImagePickerKMPState
 * @see io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
 */
@Deprecated(
    message = """
        ImagePickerLauncher belongs to the legacy API (v1) and will be removed in a future release.
        
        DELETE the entire ImagePickerLauncher { ... } block and replace it with:
        
            val picker = rememberImagePickerKMP(
                config = ImagePickerKMPConfig(
                    cropConfig          = CropConfig(enabled = true),
                    cameraCaptureConfig = CameraCaptureConfig(
                        includeExif = false,
                        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                            cancelButtonTextIOS = "Dismiss",
                            onCancelPermissionConfigIOS = { /* handle cancel */ }
                        )
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
        
        NOTE: ImagePickerConfig is only used by this legacy composable.
        You do NOT need to migrate ImagePickerConfig separately — it disappears along with ImagePickerLauncher.
    """,
    replaceWith = ReplaceWith(
        expression = "rememberImagePickerKMP()",
        imports = ["io.github.ismoy.imagepickerkmp.features.imagepicker.ui.rememberImagePickerKMP"]
    ),
    level = DeprecationLevel.WARNING
)
@Composable
expect fun ImagePickerLauncher(
    config: ImagePickerConfig
)
