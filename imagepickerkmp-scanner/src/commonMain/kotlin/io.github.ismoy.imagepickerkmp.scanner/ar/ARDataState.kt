package io.github.ismoy.imagepickerkmp.scanner.ar

sealed interface ARDataState<out T> {
    data object Loading : ARDataState<Nothing>
    data class Success<T>(val data: T) : ARDataState<T>
    data class Error(val error: Throwable) : ARDataState<Nothing>
}
