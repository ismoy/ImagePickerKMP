package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.BaseBarcodeFormatMapper
import platform.AVFoundation.*

object IOSBarcodeFormatMapper : BaseBarcodeFormatMapper<String?>() {
    init {
        registerMapping(AVMetadataObjectTypeQRCode, BarcodeFormat.QR_CODE)
        registerMapping(AVMetadataObjectTypeAztecCode, BarcodeFormat.AZTEC)
        registerMapping(AVMetadataObjectTypeCode128Code, BarcodeFormat.CODE_128)
        registerMapping(AVMetadataObjectTypeCode39Code, BarcodeFormat.CODE_39)
        registerMapping(AVMetadataObjectTypeCode93Code, BarcodeFormat.CODE_93)
        registerMapping(AVMetadataObjectTypeDataMatrixCode, BarcodeFormat.DATA_MATRIX)
        registerMapping(AVMetadataObjectTypeEAN13Code, BarcodeFormat.EAN_13)
        registerMapping(AVMetadataObjectTypeEAN8Code, BarcodeFormat.EAN_8)
        registerMapping(AVMetadataObjectTypeITF14Code, BarcodeFormat.ITF_14)
        registerMapping(AVMetadataObjectTypePDF417Code, BarcodeFormat.PDF_417)
        registerMapping(AVMetadataObjectTypeUPCECode, BarcodeFormat.UPC_E)
        registerMapping(AVMetadataObjectTypeCodabarCode, BarcodeFormat.CODABAR)
        registerMapping(AVMetadataObjectTypeCode39Mod43Code, BarcodeFormat.CODE_39_MOD_43)
        registerMapping(AVMetadataObjectTypeGS1DataBarCode, BarcodeFormat.GS1_DATA_BAR)
        registerMapping(AVMetadataObjectTypeMicroPDF417Code, BarcodeFormat.MICRO_PDF_417)
        registerMapping(AVMetadataObjectTypeMicroQRCode, BarcodeFormat.MICRO_QR_CODE)
    }
}
