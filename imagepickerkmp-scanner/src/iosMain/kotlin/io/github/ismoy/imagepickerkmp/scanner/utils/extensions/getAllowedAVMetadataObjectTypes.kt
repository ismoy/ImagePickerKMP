package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerBehaviorConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import platform.AVFoundation.AVMetadataObjectTypeAztecCode
import platform.AVFoundation.AVMetadataObjectTypeCodabarCode
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Mod43Code
import platform.AVFoundation.AVMetadataObjectTypeCode93Code
import platform.AVFoundation.AVMetadataObjectTypeDataMatrixCode
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeGS1DataBarCode
import platform.AVFoundation.AVMetadataObjectTypeITF14Code
import platform.AVFoundation.AVMetadataObjectTypeMicroPDF417Code
import platform.AVFoundation.AVMetadataObjectTypeMicroQRCode
import platform.AVFoundation.AVMetadataObjectTypePDF417Code
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.AVMetadataObjectTypeUPCECode

internal fun ScannerBehaviorConfig.getAllowedAVMetadataObjectTypes(): List<String> {
    val allowedTypes = mutableListOf<String>()

    if (allowedFormats.contains(BarcodeFormat.ALL)) {
        allowedTypes.addAll(listOfNotNull(
            AVMetadataObjectTypeEAN13Code,
            AVMetadataObjectTypeEAN8Code,
            AVMetadataObjectTypeCode128Code,
            AVMetadataObjectTypeCode39Code,
            AVMetadataObjectTypeCode93Code,
            AVMetadataObjectTypeUPCECode,
            AVMetadataObjectTypeDataMatrixCode,
            AVMetadataObjectTypeAztecCode,
            AVMetadataObjectTypePDF417Code,
            AVMetadataObjectTypeITF14Code,
            AVMetadataObjectTypeCodabarCode,
            AVMetadataObjectTypeQRCode,
            AVMetadataObjectTypeCode39Mod43Code,
            AVMetadataObjectTypeGS1DataBarCode,
            AVMetadataObjectTypeMicroPDF417Code,
            AVMetadataObjectTypeMicroQRCode
        ))
    } else {
        allowedFormats.forEach { format ->
            format.toAVMetadataObjectType()?.let { allowedTypes.add(it) }
        }
    }

    return allowedTypes
}
