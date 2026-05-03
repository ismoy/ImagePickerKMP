package io.github.ismoy.imagepickerkmp.domain.models

/**
 * Controls how the camera preview is scaled inside its viewport.
 *
 * The preview surface and the captured image share an aspect ratio (4:3), but the
 * viewport that hosts the preview typically does not. The scale type decides how
 * the preview is fitted into that mismatched viewport.
 *
 * - **FILL_** values fill the viewport entirely and crop whatever does not fit.
 *   The visible viewfinder will be narrower (or wider) than the captured image.
 * - **FIT_** values letterbox the preview so the entire camera feed is visible.
 *   The viewfinder framing matches the captured image exactly.
 *
 * Only [FILL_CENTER] and [FIT_CENTER] are commonly useful. The remaining values
 * mirror [androidx.camera.view.PreviewView.ScaleType] for completeness.
 *
 * Currently applied on Android only. iOS uses the system camera UI, where preview
 * and capture framing already match.
 */
enum class CameraScaleType {
    FILL_CENTER,
    FILL_START,
    FILL_END,
    FIT_CENTER,
    FIT_START,
    FIT_END,
}
