package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.picker.MimeType
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get

private fun currentTimeMillis(): Double = js("Date.now()")

/**
 * WasmJs platform implementation of PlatformGalleryRenderer.
 * Uses the HTML file input to open the native file picker in the browser.
 */
@Suppress("LongParameterList")
@Composable
internal actual fun PlatformGalleryRenderer(
    onPhotosSelected: (List<GalleryPhotoResult>) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>,
    selectionLimit: Long,
    cameraCaptureConfig: CameraCaptureConfig?,
    enableCrop: Boolean,
    fileFilterDescription: String,
    includeExif: Boolean,
    mimeTypeMismatchMessage: String?,
    compressionLevel: CompressionLevel?,
    onCropPending: () -> Unit
) {
    val launchKey = remember { currentTimeMillis().toString() }

    LaunchedEffect(launchKey) {
        try {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = mimeTypes.joinToString(",") { it.value }
            input.multiple = allowMultiple
            input.style.display = "none"

            document.body?.appendChild(input)

            input.addEventListener("change", { _: Event ->
                try {
                    val files = input.files
                    if (files != null && files.length > 0) {
                        val results = mutableListOf<GalleryPhotoResult>()
                        var processedCount = 0
                        val totalFiles = minOf(files.length, selectionLimit.toInt())

                        for (i in 0 until totalFiles) {
                            val file = files[i]
                            if (file != null) {
                                val reader = FileReader()
                                reader.addEventListener("load", { _: Event ->
                                    val dataUrl = reader.result.toString()
                                    results.add(
                                        GalleryPhotoResult(
                                            uri = dataUrl,
                                            fileName = file.name,
                                            fileSize = file.size.toInt().toLong(),
                                            width = 0,
                                            height = 0
                                        )
                                    )
                                    processedCount++
                                    if (processedCount == totalFiles) {
                                        onPhotosSelected(results)
                                        input.remove()
                                    }
                                })
                                reader.readAsDataURL(file)
                            } else {
                                processedCount++
                                if (processedCount == totalFiles) {
                                    onPhotosSelected(results)
                                    input.remove()
                                }
                            }
                        }
                    } else {
                        onDismiss()
                        input.remove()
                    }
                } catch (e: Exception) {
                    onError(e)
                    input.remove()
                }
            })

            input.click()
        } catch (e: Exception) {
            onError(e)
        }
    }
}
