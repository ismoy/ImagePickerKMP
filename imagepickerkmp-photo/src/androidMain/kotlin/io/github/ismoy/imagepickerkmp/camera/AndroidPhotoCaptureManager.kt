package io.github.ismoy.imagepickerkmp.camera

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.content.FileProvider
import io.github.ismoy.imagepickerkmp.picker.CompressionLevel
import io.github.ismoy.imagepickerkmp.picker.PhotoResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class AndroidPhotoCaptureManager(
    private val context: Context,
    private val fileManager: FileManager,
    private val imageProcessor: ImageProcessor
) {
    private var captureFile: File? = null
    private var pendingOnResult: ((PhotoResult) -> Unit)? = null
    private var pendingOnError: ((Exception) -> Unit)? = null
    private var pendingCompressionLevel: CompressionLevel? = null
    private var pendingIncludeExif: Boolean = false
    private var pendingRedactGps: Boolean = true

    fun buildCaptureIntent(): Pair<Intent, android.net.Uri> {
        val checkNoAppIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.packageManager.resolveActivity(checkNoAppIntent, PackageManager.MATCH_DEFAULT_ONLY)
            ?: throw ActivityNotFoundException("No app available to handle ${MediaStore.ACTION_IMAGE_CAPTURE}")

        val file = fileManager.createImageFile()
        captureFile = file
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.imagepickerkmp.photo.provider",
            file
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return intent to uri
    }

    fun setPendingCallbacks(
        onPhotoResult: (PhotoResult) -> Unit,
        onError: (Exception) -> Unit,
        compressionLevel: CompressionLevel?,
        includeExif: Boolean,
        redactGpsData: Boolean
    ) {
        pendingOnResult = onPhotoResult
        pendingOnError = onError
        pendingCompressionLevel = compressionLevel
        pendingIncludeExif = includeExif
        pendingRedactGps = redactGpsData
    }

    fun onCaptureResult(
        success: Boolean,
        scope: CoroutineScope
    ) {
        val file = captureFile ?: return
        val onResult = pendingOnResult ?: return
        val onError = pendingOnError ?: return

        if (!success) {
            file.delete()
            captureFile = null
            return
        }

        scope.launch {
            try {
                val result = imageProcessor.processImage(
                    imageFile = file,
                    compressionLevel = pendingCompressionLevel,
                    includeExif = pendingIncludeExif,
                    redactGpsData = pendingRedactGps
                )
                withContext(Dispatchers.Main) { onResult(result) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            } finally {
                captureFile = null
            }
        }
    }
}
