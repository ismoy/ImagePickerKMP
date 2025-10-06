package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import org.w3c.files.FileReader

/**
 * Converts a GalleryPhotoResult to a PhotoResult.
 * Since both models have identical structure, this is a direct mapping.
 */
private fun GalleryPhotoResult.toPhotoResult(): PhotoResult {
    return PhotoResult(
        uri = uri,
        width = width,
        height = height,
        fileName = fileName,
        fileSize = fileSize
    )
}

/**
 * Detects if the current device/context is mobile-friendly.
 * Used to determine if camera options should be preferred.
 */
private fun isMobileContext(): Boolean {
    return try {
        val userAgent = window.navigator.userAgent.lowercase()
        userAgent.contains("mobile") || 
        userAgent.contains("android") || 
        userAgent.contains("iphone") || 
        userAgent.contains("ipad") ||
        // También detectar si es una pantalla táctil
        js("'ontouchstart' in window || navigator.maxTouchPoints > 0").unsafeCast<Boolean>()
    } catch (e: Exception) {
        false
    }
}

/**
 * JS implementation for ImagePickerLauncher.
 * Uses WebRTC camera capture and file picker functionality for web browsers.
 */
@Composable
actual fun ImagePickerLauncher(
    config: ImagePickerConfig
) {
    val launchKey = remember {
        "${js("Date.now()")}-${config.hashCode()}"
    }
    
    LaunchedEffect(launchKey) {
        try {
            val hasCameraSupport = js("navigator.mediaDevices && navigator.mediaDevices.getUserMedia") != null
            val allowMultiple = config.cameraCaptureConfig.galleryConfig.allowMultiple
            val mimeTypes = config.cameraCaptureConfig.galleryConfig.mimeTypes
            val preferCamera = isMobileContext() && hasCameraSupport
            
            if (hasCameraSupport && preferCamera) {
                // Preferir cámara en móviles
                handleCameraCapture(
                    onSuccess = { result ->
                        val photoResult = PhotoResult(
                            uri = result.uri as String,
                            fileName = result.fileName as String,
                            fileSize = result.fileSize as Long,
                            width = result.width as Int,
                            height = result.height as Int
                        )
                        config.onPhotoCaptured(photoResult)
                    },
                    onError = { error ->
                        config.onError(Exception(error))
                    },
                    onCancel = config.onDismiss
                )
            } else {
                // Usar file picker como fallback
                handleFilePicker(
                    onSuccess = { result ->
                        if (allowMultiple && result.asDynamic().length != undefined) {
                            // Múltiples archivos
                            val photos = mutableListOf<GalleryPhotoResult>()
                            val resultArray = result.asDynamic()
                            for (i in 0 until resultArray.length.unsafeCast<Int>()) {
                                val item = resultArray[i]
                                photos.add(GalleryPhotoResult(
                                    uri = item.uri as String,
                                    fileName = item.fileName as String,
                                    fileSize = item.fileSize as Long,
                                    width = item.width as Int,
                                    height = item.height as Int
                                ))
                            }
                            config.onPhotosSelected?.invoke(photos)
                        } else {
                            // Un solo archivo
                            val photoResult = PhotoResult(
                                uri = result.uri as String,
                                fileName = result.fileName as String,
                                fileSize = result.fileSize as Long,
                                width = result.width as Int,
                                height = result.height as Int
                            )
                            config.onPhotoCaptured(photoResult)
                        }
                    },
                    onError = { error ->
                        config.onError(Exception(error))
                    },
                    onCancel = config.onDismiss,
                    allowMultiple = allowMultiple,
                    mimeTypes = mimeTypes
                )
            }
        } catch (e: Exception) {
            config.onError(e)
        }
    }
}

private fun handleCameraCapture(
    onSuccess: (dynamic) -> Unit,
    onError: (String) -> Unit,
    onCancel: () -> Unit
) {
    try {
        js("navigator.mediaDevices.getUserMedia")(js("""{ video: true }"""))
            ?.then { stream ->
                showCameraInterface(stream, onSuccess, onError, onCancel)
            }
            ?.catch { error ->
                val errorMessage = when {
                    error.toString().contains("NotAllowedError") -> "Camera permission denied"
                    error.toString().contains("NotFoundError") -> "No camera found"
                    error.toString().contains("NotReadableError") -> "Camera is being used by another application"
                    else -> "Error accessing camera: $error"
                }
                onError(errorMessage)
            }
    } catch (e: Exception) {
        onError(e.message ?: "Camera access failed")
    }
}

private fun showCameraInterface(
    stream: dynamic,
    onSuccess: (dynamic) -> Unit,
    onError: (String) -> Unit,
    onCancel: () -> Unit
) {
    val overlay = document.createElement("div") as HTMLDivElement
    overlay.style.apply {
        position = "fixed"
        top = "0"
        left = "0"
        width = "100vw"
        height = "100vh"
        backgroundColor = "rgba(0, 0, 0, 0.9)"
        zIndex = "10000"
        display = "flex"
        flexDirection = "column"
        alignItems = "center"
        justifyContent = "center"
    }
    
    val video = document.createElement("video") as HTMLVideoElement
    video.apply {
        srcObject = stream
        autoplay = true
        muted = true
        style.apply {
            maxWidth = "90%"
            maxHeight = "70%"
            borderRadius = "8px"
        }
    }
    
    val controls = document.createElement("div") as HTMLDivElement
    controls.style.apply {
        display = "flex"
        marginTop = "20px"
    }
    
    val captureButton = document.createElement("button") as HTMLButtonElement
    captureButton.textContent = "📷 Capture"
    captureButton.style.apply {
        padding = "12px 24px"
        fontSize = "16px"
        backgroundColor = "#007AFF"
        color = "white"
        border = "none"
        borderRadius = "8px"
        cursor = "pointer"
        marginRight = "20px"
    }
    
    val cancelButton = document.createElement("button") as HTMLButtonElement
    cancelButton.textContent = "Cancel"
    cancelButton.style.apply {
        padding = "12px 24px"
        fontSize = "16px"
        backgroundColor = "#FF3B30"
        color = "white"
        border = "none"
        borderRadius = "8px"
        cursor = "pointer"
    }
    
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.style.display = "none"
    
    captureButton.onclick = { _: Event ->
        try {
            val context = canvas.getContext("2d") as CanvasRenderingContext2D
            canvas.width = video.videoWidth
            canvas.height = video.videoHeight
            context.drawImage(video, 0.0, 0.0)
            
            canvas.toBlob({ blob: Blob? ->
                if (blob != null) {
                    val imageUrl = js("window.URL.createObjectURL")(blob) as String
                    val timestamp = js("Date.now()").toString()
                    
                    val result = js("{}")
                    result.uri = imageUrl
                    result.fileName = "camera_capture_$timestamp.png"
                    result.fileSize = blob.size.toDouble()
                    result.width = canvas.width.toDouble()
                    result.height = canvas.height.toDouble()
                    
                    cleanupCameraResources(stream, overlay)
                    onSuccess(result)
                } else {
                    onError("Failed to capture photo")
                }
            }, "image/png")
        } catch (e: Exception) {
            cleanupCameraResources(stream, overlay)
            onError(e.message ?: "Capture failed")
        }
    }
    
    cancelButton.onclick = { _: Event ->
        cleanupCameraResources(stream, overlay)
        onCancel()
    }
    
    controls.appendChild(captureButton)
    controls.appendChild(cancelButton)
    overlay.appendChild(video)
    overlay.appendChild(controls)
    overlay.appendChild(canvas)
    document.body?.appendChild(overlay)
}

private fun handleFilePicker(
    onSuccess: (dynamic) -> Unit,
    onError: (String) -> Unit,
    onCancel: () -> Unit,
    allowMultiple: Boolean,
    mimeTypes: List<MimeType>
) {
    try {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = mimeTypes.joinToString(",") { it.value }
        input.multiple = allowMultiple
        
        input.onchange = { _: Event ->
            try {
                val files = input.files
                if (files != null && files.length > 0) {
                    if (allowMultiple) {
                        val results = js("[]")
                        var processedCount = 0
                        val totalFiles = files.length
                        
                        for (i in 0 until totalFiles) {
                            val file = files.item(i)
                            if (file != null) {
                                val reader = FileReader()
                                reader.onload = { loadEvent ->
                                    val result = loadEvent.target?.asDynamic()?.result as? String
                                    if (result != null) {
                                        val fileResult = js("{}")
                                        fileResult.uri = result
                                        fileResult.fileName = file.name
                                        fileResult.fileSize = file.size.toDouble()
                                        fileResult.width = 0.0
                                        fileResult.height = 0.0
                                        
                                        results.push(fileResult)
                                    }
                                    
                                    processedCount++
                                    if (processedCount == totalFiles) {
                                        onSuccess(results)
                                    }
                                }
                                reader.readAsDataURL(file)
                            }
                        }
                    } else {
                        // Single file
                        val file = files.item(0)
                        if (file != null) {
                            val reader = FileReader()
                            reader.onload = { loadEvent ->
                                val result = loadEvent.target?.asDynamic()?.result as? String
                                if (result != null) {
                                    val fileResult = js("{}")
                                    fileResult.uri = result
                                    fileResult.fileName = file.name
                                    fileResult.fileSize = file.size.toDouble()
                                    fileResult.width = 0.0
                                    fileResult.height = 0.0
                                    
                                    onSuccess(fileResult)
                                } else {
                                    onError("Failed to read file")
                                }
                            }
                            reader.readAsDataURL(file)
                        }
                    }
                } else {
                    onCancel()
                }
            } catch (e: Exception) {
                onError(e.message ?: "File selection failed")
            }
        }
        
        input.click()
    } catch (e: Exception) {
        onError(e.message ?: "Failed to open file picker")
    }
}

private fun cleanupCameraResources(stream: dynamic, overlay: HTMLElement) {
    try {
        val tracks = stream.getTracks()
        for (i in 0 until tracks.length) {
            tracks[i].stop()
        }
        overlay.remove()
    } catch (e: Exception) {
        console.error("Error during cleanup: $e")
    }
}
