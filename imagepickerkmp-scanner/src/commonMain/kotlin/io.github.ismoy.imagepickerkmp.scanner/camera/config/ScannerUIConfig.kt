package io.github.ismoy.imagepickerkmp.scanner.camera.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.scanner.I18nKonfig

data class ScannerUIConfig(
    val watermark: String = "ImagePickerKMP",
    val tooFarColor: Color = Color.Red,
    val tooCloseColor: Color = Color.Red,
    val tooOptimalColor: Color = Color.Green,
    val tooFarText: String = I18nKonfig.General.scanner_too_far,
    val tooCloseText: String = I18nKonfig.General.scanner_too_close,
    val tooOptimalText: String = I18nKonfig.General.scanner_optimal,
    val doneText: String = I18nKonfig.General.scanner_done,
    val inactiveModeText: String = I18nKonfig.General.scanner_pause_tap_to_continue_text,
    val showScanLine: Boolean = true,
    val scanLineColor: Color = Color.Red,
    val overlayCornerRadius: Dp = 12.dp,
    val overlayStyle: ScannerOverlayStyle = ScannerOverlayStyle.CLASSIC,
    val inactiveOverlayStyle: InactiveOverlayStyle = InactiveOverlayStyle.SIMPLE,
    val enterpriseOverlayConfig: EnterpriseOverlayConfig = EnterpriseOverlayConfig(),
    val backgroundHeaderScanner: Color = Color.Black.copy(alpha = 0.7f),
    val paddingHeaderScanner: PaddingValues = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 10.dp),
    val paddingBottomScanner: PaddingValues = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 10.dp),
    val iconCloseSizeHeaderScanner: Int = 48,
    val iconFlashSizeHeaderScanner: Int = 48
)
