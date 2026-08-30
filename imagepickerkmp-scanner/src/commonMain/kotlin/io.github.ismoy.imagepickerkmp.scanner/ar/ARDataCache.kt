package io.github.ismoy.imagepickerkmp.scanner.ar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface ARDataCache<T> {
    @Composable
    fun getDataState(barcode: String): ARDataState<T>?
    fun clear()
    fun invalidate(barcode: String)
}

@Composable
fun <T> rememberARDataCache(
    loader: suspend (String) -> T
): ARDataCache<T> {
    val coroutineScope = rememberCoroutineScope()
    val cache = remember { ARDataCacheImpl(coroutineScope, loader) }
    return cache
}

internal class ARDataCacheImpl<T>(
    private val scope: CoroutineScope,
    private val loader: suspend (String) -> T
) : ARDataCache<T> {

    private val cacheState = MutableStateFlow<Map<String, ARDataState<T>>>(emptyMap())

    @Composable
    override fun getDataState(barcode: String): ARDataState<T>? {
        val map by cacheState.collectAsState()

        LaunchedEffect(barcode) {
            if (!cacheState.value.containsKey(barcode)) {
                fetch(barcode)
            }
        }

        return map[barcode]
    }

    override fun clear() {
        cacheState.value = emptyMap()
    }

    override fun invalidate(barcode: String) {
        val mutable = cacheState.value.toMutableMap()
        mutable.remove(barcode)
        cacheState.value = mutable
    }

    private fun fetch(barcode: String) {
        val current = cacheState.value
        if (current[barcode] is ARDataState.Loading) return

        cacheState.value = current + (barcode to ARDataState.Loading)

        scope.launch {
            try {
                val result = loader(barcode)
                cacheState.value = cacheState.value + (barcode to ARDataState.Success(result))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                cacheState.value = cacheState.value + (barcode to ARDataState.Error(exception))
            }
        }
    }
}
