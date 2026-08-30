package io.github.ismoy.imagepickerkmp.scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.camera.ScannerCameraStateHolder

@Composable
internal fun ScannerBottomBar(
    modifier: Modifier = Modifier,
    config: ScannerCameraConfig,
    uiExtensions: ScannerUIExtensions = ScannerUIExtensions(),
    stateHolder: ScannerCameraStateHolder,
    scannedCount: Int,
    onBatchDone: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(config.ui.backgroundHeaderScanner)
            .padding(config.ui.paddingBottomScanner),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ScannerDistanceWarning(config = config, distance = stateHolder.distance)

        if (config.advanced.batchMode) {
            ScannerBatchControls(
                config = config,
                uiExtensions = uiExtensions,
                stateHolder = stateHolder,
                scannedCount = scannedCount,
                onBatchDone = onBatchDone
            )
        }
    }
}
