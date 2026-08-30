package io.github.ismoy.imagepickerkmp.scanner.utils.extensions

import io.github.ismoy.imagepickerkmp.scanner.IOSBarcodeFormatMapper
import io.github.ismoy.imagepickerkmp.scanner.domain.BarcodeFormat

internal fun String.toDomainFormat(): BarcodeFormat = IOSBarcodeFormatMapper.getDomainFormat(this)
