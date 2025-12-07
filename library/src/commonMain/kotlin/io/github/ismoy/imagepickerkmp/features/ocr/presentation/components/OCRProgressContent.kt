package io.github.ismoy.imagepickerkmp.presentation.ui.components.ocr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.features.ocr.model.ExtractionIndicators
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRProcessState

@Composable
 fun OCRProgressContent(
    currentState: OCRProcessState,
    providerName: String,
    extractionIndicators: ExtractionIndicators,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 16.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentState) {
                OCRProcessState.UPLOADING -> {
                    UploadingContent()
                }
                OCRProcessState.PROCESSING -> {
                    ProcessingContent(providerName,extractionIndicators)
                }
                OCRProcessState.SUCCESS -> {
                    SuccessContent(onDismiss)
                }
                OCRProcessState.ERROR -> {
                    ErrorContent(errorMessage, onDismiss)
                }
            }
        }
    }
}
