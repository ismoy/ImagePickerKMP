
package io.github.ismoy.imagepickerkmp.data.processors

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import kotlin.math.min
import androidx.core.graphics.createBitmap
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants.NUMBER_ZERO

internal fun createCircularBitmap(bitmap: Bitmap): Bitmap {
    val size = min(bitmap.width, bitmap.height)
    val output = createBitmap(size, size)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
    }

    val rect = android.graphics.Rect(NUMBER_ZERO, NUMBER_ZERO, size, size)
    val rectF = android.graphics.RectF(rect)

    canvas.drawOval(rectF, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    return output
}
