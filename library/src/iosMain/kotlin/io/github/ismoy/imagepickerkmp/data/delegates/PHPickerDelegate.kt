package io.github.ismoy.imagepickerkmp.data.delegates

import io.github.ismoy.imagepickerkmp.data.processors.ImageProcessor
import io.github.ismoy.imagepickerkmp.domain.utils.ExifDataExtractor
import io.github.ismoy.imagepickerkmp.domain.models.CompressionLevel
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.Photos.PHAsset
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Delegate for handling PHPickerViewController events and photo selection results on iOS.
 */
@OptIn(ExperimentalForeignApi::class)
class PHPickerDelegate(
    private val onPhotoSelected: (GalleryPhotoResult) -> Unit,
    private val onPhotosSelected: ((List<GalleryPhotoResult>) -> Unit)? = null,
    private val onError: (Exception) -> Unit,
    private val onDismiss: () -> Unit,
    private val compressionLevel: CompressionLevel? = null,
    private val includeExif: Boolean = false
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    private val mutex = Mutex()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun picker(
        picker: PHPickerViewController,
        didFinishPicking: List<*>
    ) {
        println("📸 PHPickerDelegate: picker:didFinishPicking called with ${didFinishPicking.size} items")
        
        // Dismiss picker immediately to avoid UI freeze
        dismissPicker(picker)
        
        val resultsList = didFinishPicking
        if (resultsList.isEmpty()) {
            dispatch_async(dispatch_get_main_queue()) {
                onDismiss()
            }
            return
        }

        val totalCount = resultsList.size
        var processedCount = 0
        val resultsPhotos = mutableListOf<GalleryPhotoResult>()

        suspend fun checkAndFinishSafe() {
            mutex.withLock {
                if (processedCount >= totalCount) {
                    println("✅ PHPickerDelegate: All $totalCount images processed. Results: ${resultsPhotos.size}")
                    dispatch_async(dispatch_get_main_queue()) {
                        if (resultsPhotos.isNotEmpty()) {
                            if (onPhotosSelected != null) {
                                onPhotosSelected.invoke(resultsPhotos)
                            } else {
                                resultsPhotos.forEach(onPhotoSelected)
                            }
                        }
                    }
                }
            }
        }

                // Process all images in background
        for ((index, result) in resultsList.withIndex()) {
            println("🔄 PHPickerDelegate: Processing image $index of $totalCount")
            val pickerResult = result as? PHPickerResult
            if (pickerResult == null) {
                CoroutineScope(Dispatchers.Default).launch {
                    mutex.withLock {
                        processedCount++
                    }
                    dispatch_async(dispatch_get_main_queue()) {
                        onError(Exception("Invalid picker result"))
                    }
                    checkAndFinishSafe()
                }
                continue
            }

            pickerResult.itemProvider.loadFileRepresentationForTypeIdentifier(
                "public.image"
            ) { url, error ->
                if (error != null || url == null) {
                    CoroutineScope(Dispatchers.Default).launch {
                        mutex.withLock {
                            processedCount++
                        }
                        dispatch_async(dispatch_get_main_queue()) {
                            onError(Exception("Failed to load image"))
                        }
                        checkAndFinishSafe()
                    }
                    return@loadFileRepresentationForTypeIdentifier
                }

                // Process image in background thread
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        println("⚙️ PHPickerDelegate: Processing image data for index $index")
                        val imageData = NSData.dataWithContentsOfURL(url)
                        val uiImage = imageData?.let { UIImage.imageWithData(it) }

                        if (uiImage != null) {
                            val processedData = if (compressionLevel != null) {
                                ImageProcessor.processImageForGallery(uiImage, compressionLevel)
                            } else {
                                UIImageJPEGRepresentation(uiImage, 1.0)
                            }
                            
                            if (processedData != null) {
                                val tempURL = ImageProcessor.saveImageToTempDirectory(processedData)
                                if (tempURL != null) {
                                    val fileSizeInBytes = processedData.length.toLong()
                                    val fileSizeInKB = fileSizeInBytes / 1024
                                    
                                    // Extract EXIF data in background thread
                                    val exifData = if (includeExif) {
                                        try {
                                            val assetIdentifier = pickerResult.assetIdentifier
                                            if (assetIdentifier != null) {
                                                val fetchResult = platform.Photos.PHAsset.fetchAssetsWithLocalIdentifiers(
                                                    listOf(assetIdentifier),
                                                    null
                                                )
                                                val asset = fetchResult.firstObject() as? PHAsset
                                                
                                                if (asset != null) {
                                                    ExifDataExtractor.extractExifDataFromAsset(asset)
                                                } else {
                                                    ExifDataExtractor.extractExifData(tempURL.path ?: "")
                                                }
                                            } else {
                                                ExifDataExtractor.extractExifData(tempURL.path ?: "")
                                            }
                                        } catch (e: Exception) {
                                            println("⚠️ iOS PHPickerDelegate: EXIF extraction failed: ${e.message}")
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                    
                                    // Add result to list (thread-safe)
                                    val galleryResult = GalleryPhotoResult(
                                        uri = tempURL.absoluteString ?: "",
                                        width = uiImage.size.useContents { width.toInt() },
                                        height = uiImage.size.useContents { height.toInt() },
                                        fileName = tempURL.lastPathComponent,
                                        fileSize = fileSizeInKB,
                                        mimeType = "image/jpeg",
                                        exif = exifData
                                    )
                                    
                                    mutex.withLock {
                                        resultsPhotos.add(galleryResult)
                                        processedCount++
                                        println("✅ PHPickerDelegate: Image $index processed ($processedCount/$totalCount)")
                                    }
                                    checkAndFinishSafe()
                                } else {
                                    mutex.withLock {
                                        processedCount++
                                    }
                                    withContext(Dispatchers.Main) {
                                        onError(Exception("Failed to save processed image"))
                                    }
                                    checkAndFinishSafe()
                                }
                            } else {
                                mutex.withLock {
                                    processedCount++
                                }
                                withContext(Dispatchers.Main) {
                                    onError(Exception("Failed to process image"))
                                }
                                checkAndFinishSafe()
                            }
                        } else {
                            mutex.withLock {
                                processedCount++
                            }
                            withContext(Dispatchers.Main) {
                                onError(Exception("Failed to create UIImage"))
                            }
                            checkAndFinishSafe()
                        }
                    } catch (e: Exception) {
                        println("❌ PHPickerDelegate: Exception processing image $index: ${e.message}")
                        mutex.withLock {
                            processedCount++
                        }
                        withContext(Dispatchers.Main) {
                            onError(Exception("Error processing image: ${e.message}"))
                        }
                        checkAndFinishSafe()
                    }
                }
            }
        }
    }

    private fun dismissPicker(picker: PHPickerViewController) {
        dispatch_async(dispatch_get_main_queue()) {
            picker.dismissViewControllerAnimated(true, completion = null)
        }
    }
}
