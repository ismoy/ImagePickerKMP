package io.github.ismoy.imagepickerkmp.picker

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.I18nKonfig
import io.github.ismoy.imagepickerkmp.core.language.getLanguageDevice
import io.github.ismoy.imagepickerkmp.config.CameraCaptureConfig

@Stable
class ImagePickerKMPState internal constructor(
    internal val config: ImagePickerKMPConfig
) {
    init {
        I18nKonfig.setLocale(getLanguageDevice())
    }

    var result: ImagePickerResult by mutableStateOf(ImagePickerResult.Idle)
        private set
    var isCropActive: Boolean by mutableStateOf(false)
        private set

    internal var activeMode by mutableStateOf<PickerMode>(PickerMode.None)

    private var consumerOnDismiss: (() -> Unit)? = null
    private var consumerOnError: ((Exception) -> Unit)? = null

    fun reset() {
        result = ImagePickerResult.Idle
        isCropActive = false
        activeMode = PickerMode.None
        consumerOnDismiss = null
        consumerOnError = null
    }

    fun launchCamera(
        cameraCaptureConfig: CameraCaptureConfig? = null,
        onDismiss: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        if (activeMode != PickerMode.None) {
            if (result is ImagePickerResult.Dismissed ||
                result is ImagePickerResult.Error ||
                result is ImagePickerResult.Success) {
                activeMode = PickerMode.None
            } else {
                return
            }
        }
        result = ImagePickerResult.Loading
        consumerOnDismiss = onDismiss
        consumerOnError = onError
        activeMode = PickerMode.Camera(
            cameraCaptureConfig = cameraCaptureConfig ?: config.cameraCaptureConfig,
            enableCrop = config.cropConfig.enabled
        )
    }

    @Suppress("LongParameterList")
    fun launchGallery(
        allowMultiple: Boolean? = null,
        mimeTypes: List<MimeType>? = null,
        selectionLimit: Int? = null,
        includeExif: Boolean? = null,
        redactGpsData: Boolean? = null,
        mimeTypeMismatchMessage: String? = null,
        compressionLevel: CompressionLevel? = null,
        cameraCaptureConfig: CameraCaptureConfig? = null,
        onDismiss: (() -> Unit)? = null,
        onError: ((Exception) -> Unit)? = null
    ) {
        if (activeMode != PickerMode.None) {
            if (result is ImagePickerResult.Dismissed ||
                result is ImagePickerResult.Error ||
                result is ImagePickerResult.Success) {
                activeMode = PickerMode.None
            } else {
                return
            }
        }
        result = ImagePickerResult.Loading
        consumerOnDismiss = onDismiss
        consumerOnError = onError
        activeMode = PickerMode.Gallery(
            allowMultiple = allowMultiple ?: config.galleryConfig.allowMultiple,
            mimeTypes = mimeTypes ?: config.galleryConfig.mimeTypes,
            selectionLimit = (selectionLimit ?: config.galleryConfig.selectionLimit).toLong(),
            enableCrop = config.cropConfig.enabled,
            includeExif = includeExif ?: config.galleryConfig.includeExif,
            redactGpsData = redactGpsData ?: config.galleryConfig.redactGpsData,
            mimeTypeMismatchMessage = mimeTypeMismatchMessage ?: config.galleryConfig.mimeTypeMismatchMessage,
            compressionLevel = compressionLevel ?: config.galleryConfig.compressionLevel,
            cameraCaptureConfig = cameraCaptureConfig
        )
    }

    internal fun notifySuccess(photos: List<PhotoResult>) {
        isCropActive = false
        result = ImagePickerResult.Success(photos)
        activeMode = PickerMode.None
    }

    internal fun notifyDismiss() {
        isCropActive = false
        // A dismiss that follows an error is just teardown; keep the reason visible.
        if (result !is ImagePickerResult.Error) {
            result = ImagePickerResult.Dismissed
        }
        activeMode = PickerMode.None
    }

    internal fun onError(exception: Exception) {
        isCropActive = false
        result = ImagePickerResult.Error(exception)
        activeMode = PickerMode.None
    }

    internal fun notifyCropPending() {
        isCropActive = true
        result = ImagePickerResult.Idle
    }

    internal fun dispatchDismiss() {
        notifyDismiss()
        consumerOnDismiss?.invoke()
    }

    internal fun dispatchError(exception: Exception) {
        onError(exception)
        consumerOnError?.invoke(exception)
    }
}
