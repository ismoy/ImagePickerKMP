
package io.github.ismoy.imagepickerkmp.domain.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.createBitmap

 fun createTransparentBitmap(bitmap: Bitmap): Bitmap {
    val output = createBitmap(bitmap.width, bitmap.height)
    val canvas = Canvas(output)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    return output
}
