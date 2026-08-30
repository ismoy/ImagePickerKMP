package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
internal fun ScannerZoomControl(
    zoomProgress: Float,
    currentZoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var width by remember { mutableStateOf(0f) }

    val formattedCurrent = ((currentZoom * 10f).roundToInt() / 10f).toString()

    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${formattedCurrent}x",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(48.dp)
                .onSizeChanged { width = it.width.toFloat() }
                .pointerInput(Unit) {
                   awaitEachGesture {
                        val down = awaitFirstDown()
                        if (width > 0) {
                            val p = down.position.x / width
                            onZoomChange(p.coerceIn(0f, 1f))
                        }

                        var pressed = true
                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                change.consume()
                                if (width > 0) {
                                    val p = change.position.x / width
                                    onZoomChange(p.coerceIn(0f, 1f))
                                }
                            } else {
                                pressed = false
                            }
                        }
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(zoomProgress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
                    .align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier
                    .offset {
                        val maxOffset = if (width > 0) width else 200.dp.toPx()
                        val thumbRadius = 16.dp.toPx() / 2
                        val xOffset = (maxOffset * zoomProgress) - thumbRadius
                        IntOffset(x = xOffset.roundToInt(), y = 0)
                    }
                    .size(16.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.CenterStart)
            )
        }
    }
}
