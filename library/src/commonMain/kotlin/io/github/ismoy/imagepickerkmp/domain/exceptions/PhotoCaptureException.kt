package io.github.ismoy.imagepickerkmp.domain.exceptions

internal sealed class ImagePickerException(message: String, cause: Throwable? = null) : Exception(message, cause)

internal class PhotoCaptureException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)

internal class PermissionDeniedException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)

internal class ImageProcessingException(message: String, cause: Throwable? = null) : ImagePickerException(message, cause)
