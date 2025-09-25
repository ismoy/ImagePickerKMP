package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Defines the different crop handles available for image cropping operations.
 * These handles represent the different points where users can interact to resize the crop area.
 */
enum class CropHandle {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
    TOP_CENTER, BOTTOM_CENTER, LEFT_CENTER, RIGHT_CENTER
}