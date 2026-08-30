package io.github.ismoy.imagepickerkmp.scanner

import com.google.mlkit.vision.barcode.common.Barcode
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat
import io.github.ismoy.imagepickerkmp.scanner.domain.BaseBarcodeFormatMapper

object MLKitBarcodeFormatMapper : BaseBarcodeFormatMapper<Int>() {
    init {
        registerMapping(Barcode.FORMAT_QR_CODE, BarcodeFormat.QR_CODE)
        registerMapping(Barcode.FORMAT_AZTEC, BarcodeFormat.AZTEC)
        registerMapping(Barcode.FORMAT_CODE_128, BarcodeFormat.CODE_128)
        registerMapping(Barcode.FORMAT_CODE_39, BarcodeFormat.CODE_39)
        registerMapping(Barcode.FORMAT_CODE_93, BarcodeFormat.CODE_93)
        registerMapping(Barcode.FORMAT_CODABAR, BarcodeFormat.CODABAR)
        registerMapping(Barcode.FORMAT_DATA_MATRIX, BarcodeFormat.DATA_MATRIX)
        registerMapping(Barcode.FORMAT_EAN_13, BarcodeFormat.EAN_13)
        registerMapping(Barcode.FORMAT_EAN_8, BarcodeFormat.EAN_8)
        registerMapping(Barcode.FORMAT_ITF, BarcodeFormat.ITF)
        registerMapping(Barcode.FORMAT_PDF417, BarcodeFormat.PDF_417)
        registerMapping(Barcode.FORMAT_UPC_A, BarcodeFormat.UPC_A)
        registerMapping(Barcode.FORMAT_UPC_E, BarcodeFormat.UPC_E)
    }
}
