package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.ui.geometry.Rect

 internal val Rect.centerX: Float get() = left + width / 2
 internal val Rect.centerY: Float get() = top + height / 2