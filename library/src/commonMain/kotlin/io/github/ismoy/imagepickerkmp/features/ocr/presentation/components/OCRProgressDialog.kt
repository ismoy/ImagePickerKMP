package io.github.ismoy.imagepickerkmp.features.ocr.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.ismoy.imagepickerkmp.features.ocr.data.providers.CloudOCRProvider
import io.github.ismoy.imagepickerkmp.features.ocr.model.ExtractionIndicators
import io.github.ismoy.imagepickerkmp.features.ocr.model.OCRProcessState
import io.github.ismoy.imagepickerkmp.domain.utils.getProviderDisplayName
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ocr.OCRProgressContent


@Composable
fun OCRProgressDialog(
    isVisible: Boolean,
    currentState: OCRProcessState,
    provider: CloudOCRProvider? = null,
    providerName: String = getProviderDisplayName(provider),
    extractionIndicators: ExtractionIndicators,
    errorMessage: String? = null,
    onDismiss: () -> Unit = {}
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = { 
                if (currentState == OCRProcessState.SUCCESS || currentState == OCRProcessState.ERROR) {
                    onDismiss()
                }
            },
            properties = DialogProperties(
                dismissOnClickOutside = currentState == OCRProcessState.SUCCESS || currentState == OCRProcessState.ERROR,
                dismissOnBackPress = false
            )
        ) {
            OCRProgressContent(
                currentState = currentState,
                providerName = providerName,
                extractionIndicators = extractionIndicators,
                errorMessage = errorMessage,
                onDismiss = onDismiss
            )
        }
    }
}
