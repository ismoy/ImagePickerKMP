package io.github.ismoy.imagepickerkmp.scanner

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage
import platform.Vision.VNBarcodeObservation
import platform.Vision.VNDetectBarcodesRequest
import platform.Vision.VNImageRequestHandler
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
internal class IOSStaticCodeScanner : StaticCodeScanner {
    override suspend fun scanImage(imageBytes: ByteArray): String? {
        val nsData = imageBytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), imageBytes.size.toULong())
        }
        val uiImage = UIImage(data = nsData)
        val cgImage = uiImage.CGImage ?: return null

        return suspendCoroutine { continuation ->
            val request = VNDetectBarcodesRequest { request, error ->
                if (error != null) {
                    continuation.resume(null)
                    return@VNDetectBarcodesRequest
                }

                val results = request?.results?.filterIsInstance<VNBarcodeObservation>()
                val firstResult = results?.firstOrNull()?.payloadStringValue
                continuation.resume(firstResult)
            }

            val handler = VNImageRequestHandler(cGImage = cgImage, options = emptyMap<Any?, Any>())
            try {
                handler.performRequests(listOf(request), null)
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }
}