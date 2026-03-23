
package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO_FLOAT

internal fun createTransparentBitmap(bitmap: Bitmap): Bitmap {
    val output = createBitmap(bitmap.width, bitmap.height)
    val canvas = Canvas(output)
    canvas.drawBitmap(bitmap, NUMBER_ZERO_FLOAT, NUMBER_ZERO_FLOAT, null)
    return output
}
