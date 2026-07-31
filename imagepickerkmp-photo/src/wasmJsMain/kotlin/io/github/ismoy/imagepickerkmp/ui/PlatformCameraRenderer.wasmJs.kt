package io.github.ismoy.imagepickerkmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get

@OptIn(ExperimentalWasmJsInterop::class)
private fun currentTimeMillis(): Double = js("Date.now()")

/**
 * WasmJs platform implementation of PlatformCameraRenderer.
 * Uses HTML file input with capture attribute to request camera on mobile browsers.
 * Falls back to a file picker for images on desktop browsers.
 */
@OptIn(ExperimentalWasmJsInterop::class)
@Composable
internal actual fun PlatformCameraRenderer(
    config: ImagePickerConfig
) {
    val launchKey = remember { currentTimeMillis().toString() }

    LaunchedEffect(launchKey) {
        try {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/*"
            input.setAttribute("capture", "environment")
            input.style.display = "none"

            document.body?.appendChild(input)

            input.addEventListener("change", { _: Event ->
                try {
                    val files = input.files
                    if (files != null && files.length > 0) {
                        val file = files[0]
                        if (file != null) {
                            val reader = FileReader()
                            reader.addEventListener("load", { _: Event ->
                                val dataUrl = reader.result.toString()
                                val photoResult = PhotoResult(
                                    uri = dataUrl,
                                    fileName = file.name,
                                    fileSize = file.size.toInt().toLong(),
                                    width = 0,
                                    height = 0
                                )
                                config.onPhotoCaptured(photoResult)
                                input.remove()
                            })
                            reader.readAsDataURL(file)
                        } else {
                            config.onDismiss()
                            input.remove()
                        }
                    } else {
                        config.onDismiss()
                        input.remove()
                    }
                } catch (e: Exception) {
                    config.onError(e)
                    input.remove()
                }
            })

            input.click()
        } catch (e: Exception) {
            config.onError(e)
        }
    }
}
