package io.github.ismoy.imagepickerkmp.presentation.ui.components.ocr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.features.ocr.model.ExtractionIndicators


@Composable
 fun OCRFeatureIndicators( extractionIndicators: ExtractionIndicators) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FeatureIndicator(extractionIndicators.ocrIndicatorText, extractionIndicators.ocrIndicatorEmoji)
        FeatureIndicator(extractionIndicators.structuredIndicatorText, extractionIndicators.structuredIndicatorEmoji)
        FeatureIndicator(extractionIndicators.iaIndicatorText, extractionIndicators.iaIndicatorEmoji)
    }
}
