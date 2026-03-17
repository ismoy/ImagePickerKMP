package io.github.ismoy.imagepickerkmp.presentation.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun VolumeButtonCapture(onVolumePressed: () -> Unit) {
    val onVolumePressedRef = remember { mutableRefOf(onVolumePressed) }
    onVolumePressedRef.value = onVolumePressed

    AndroidView(
        modifier = Modifier,
        factory = { context: Context ->
            VolumeKeyView(context) { onVolumePressedRef.value() }
        },
        update = {}
    )
}

private fun mutableRefOf(value: () -> Unit) = object {
    var value: () -> Unit = value
}

private class VolumeKeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val onVolumePressed: () -> Unit = {}
) : View(context, attrs, defStyleAttr) {

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requestFocus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            onVolumePressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
