package io.github.ismoy.imagepickerkmp.features.ocr.model

import io.github.ismoy.imagepickerkmp.domain.models.MimeType

/**
 * Configuration for ImagePickerLauncherOCR
 *
 * @param scanMode OCR scanning mode (Cloud with API key)
 * @param onOCRCompleted Callback with OCR results when image is captured and analyzed
 * @param onError Callback for handling errors during image capture or OCR analysis
 * @param onCancel Callback when user cancels the operation
 * @param directCameraLaunch If true, launches camera directly without selection dialog
 * @param enableCrop If true, allows image cropping before OCR analysis
 * @param dialogTitle Title for the selection dialog (camera/gallery choice)
 * @param takePhotoText Text for "Take Photo" option
 * @param selectFromGalleryText Text for "Select from Gallery" option
 * @param cancelText Text for "Cancel" option
 * @param allowedMimeTypes List of MIME types to allow in gallery (defaults to all images + PDF)
 * @param extractionIndicators Custom extraction indicators for text and emojis
 */
data class ImagePickerOCRConfig(
    val scanMode: ScanMode,
    val onOCRCompleted: (OCRResult) -> Unit,
    val onError: (Exception) -> Unit,
    val onCancel: () -> Unit = {},
    val directCameraLaunch: Boolean = false,
    val enableCrop: Boolean = false,
    val dialogTitle: String = "Select Image Source",
    val takePhotoText: String = "Take Photo",
    val selectFromGalleryText: String = "Select from Gallery",
    val cancelText: String = "Cancel",
    val allowedMimeTypes: List<MimeType> = listOf(MimeType.IMAGE_ALL, MimeType.APPLICATION_PDF),
    val extractionIndicators: ExtractionIndicators = ExtractionIndicators()
)