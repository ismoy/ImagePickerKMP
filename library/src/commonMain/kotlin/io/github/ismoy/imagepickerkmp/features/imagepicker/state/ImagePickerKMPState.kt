package io.github.ismoy.imagepickerkmp.features.imagepicker.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.ismoy.imagepickerkmp.domain.config.CameraCaptureConfig
import io.github.ismoy.imagepickerkmp.domain.models.MimeType
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.config.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.ImagePickerResult
import io.github.ismoy.imagepickerkmp.features.imagepicker.model.PickerMode

@Stable
class ImagePickerKMPState internal constructor(
    internal val config: ImagePickerKMPConfig
) {
    var result: ImagePickerResult by mutableStateOf(ImagePickerResult.Idle)
        private set
    var isCropActive: Boolean by mutableStateOf(false)
        private set

    internal var activeMode by mutableStateOf<PickerMode>(PickerMode.None)
    fun reset() {
        result = ImagePickerResult.Idle
        isCropActive = false
        activeMode = PickerMode.None
    }

    /**
     * Launches the camera picker.
     *
     * All parameters are optional. When provided, they override the corresponding values in the
     * global [ImagePickerKMPConfig] for this single invocation only, without mutating the
     * remembered configuration.
     *
     * If a picker session is already active and its result has not yet been consumed, this call
     * is ignored. Once the result transitions to [ImagePickerResult.Success],
     * [ImagePickerResult.Dismissed], or [ImagePickerResult.Error], a new session can be started.
     *
     * @param cameraCaptureConfig Per-launch camera configuration override. When `null`, the value
     *   from [ImagePickerKMPConfig.cameraCaptureConfig] is used.
     * @param onDismiss Callback invoked when the user closes the camera without capturing.
     *   When `null`, the state transitions to [ImagePickerResult.Dismissed] automatically.
     * @param onError Callback invoked if an error occurs during capture. When `null`, the state
     *   transitions to [ImagePickerResult.Error] automatically.
     *
     * @see launchGallery
     * @see reset
     */
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
        activeMode = PickerMode.Camera(
            cameraCaptureConfig = cameraCaptureConfig ?: config.cameraCaptureConfig,
            enableCrop = config.cropConfig.enabled,
            onDismiss = onDismiss,
            onError = onError
        )
    }

    /**
     * Launches the gallery picker.
     *
     * All parameters are optional. When provided, they override the corresponding values in the
     * global [ImagePickerKMPConfig] for this single invocation only, without mutating the
     * remembered configuration.
     *
     * If a picker session is already active and its result has not yet been consumed, this call
     * is ignored. Once the result transitions to [ImagePickerResult.Success],
     * [ImagePickerResult.Dismissed], or [ImagePickerResult.Error], a new session can be started.
     *
     * @param allowMultiple Whether multiple files can be selected in a single session.
     *   When `null`, falls back to [ImagePickerKMPConfig.galleryConfig] `allowMultiple`.
     * @param mimeTypes List of accepted MIME types used to filter the gallery content.
     *   When `null`, falls back to [ImagePickerKMPConfig.galleryConfig] `mimeTypes`.
     * @param selectionLimit Maximum number of files the user can select. Only relevant when
     *   [allowMultiple] is `true`. On iOS this is enforced by the system picker; on Android
     *   there is no system-level limit. When `null`, falls back to
     *   [ImagePickerKMPConfig.galleryConfig] `selectionLimit`.
     * @param includeExif Whether EXIF metadata (camera model, timestamps, GPS) should be
     *   extracted and included in each [PhotoResult]. When `null`, falls back to
     *   [ImagePickerKMPConfig.galleryConfig] `includeExif`.
     * @param redactGpsData When `true`, strips GPS coordinates (latitude, longitude, altitude)
     *   from the extracted EXIF data before delivering results. Only applies when
     *   [includeExif] is `true`. When `null`, falls back to
     *   [ImagePickerKMPConfig.galleryConfig] `redactGpsData`.
     * @param mimeTypeMismatchMessage Custom message shown to the user when a selected file does
     *   not match the allowed [mimeTypes]. When `null`, falls back to
     *   [ImagePickerKMPConfig.galleryConfig] `mimeTypeMismatchMessage`.
     * @param cameraCaptureConfig Camera configuration used when the picker integrates an
     *   in-gallery camera button. Pass `null` to disable the camera shortcut.
     * @param onDismiss Callback invoked when the user closes the gallery without selecting.
     *   When `null`, the state transitions to [ImagePickerResult.Dismissed] automatically.
     * @param onError Callback invoked if an error occurs during selection. When `null`, the
     *   state transitions to [ImagePickerResult.Error] automatically.
     *
     * @see launchCamera
     * @see reset
     */
    @Suppress("LongParameterList")
    fun launchGallery(
        allowMultiple: Boolean? = null,
        mimeTypes: List<MimeType>? = null,
        selectionLimit: Int? = null,
        includeExif: Boolean? = null,
        redactGpsData: Boolean? = null,
        mimeTypeMismatchMessage: String? = null,
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
        activeMode = PickerMode.Gallery(
            allowMultiple = allowMultiple ?: config.galleryConfig.allowMultiple,
            mimeTypes = mimeTypes ?: config.galleryConfig.mimeTypes,
            selectionLimit = (selectionLimit ?: config.galleryConfig.selectionLimit).toLong(),
            enableCrop = config.cropConfig.enabled,
            includeExif = includeExif ?: config.galleryConfig.includeExif,
            redactGpsData = redactGpsData ?: config.galleryConfig.redactGpsData,
            mimeTypeMismatchMessage = mimeTypeMismatchMessage ?: config.galleryConfig.mimeTypeMismatchMessage,
            cameraCaptureConfig = cameraCaptureConfig,
            onDismiss = onDismiss,
            onError = onError
        )
    }

    internal fun notifySuccess(photos: List<PhotoResult>) {
        isCropActive = false
        result = ImagePickerResult.Success(photos)
        activeMode = PickerMode.None
    }

    internal fun notifyDismiss() {
        isCropActive = false
        result = ImagePickerResult.Dismissed
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
}
