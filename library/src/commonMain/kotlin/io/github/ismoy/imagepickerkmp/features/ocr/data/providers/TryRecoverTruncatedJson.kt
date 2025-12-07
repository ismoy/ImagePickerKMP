package io.github.ismoy.imagepickerkmp.features.ocr.data.providers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

 fun tryRecoverTruncatedJson(jsonText: String): JsonObject? {
    try {
        var bracketCount = 0
        var lastCompleteIndex = -1

        for (i in jsonText.indices) {
            when (jsonText[i]) {
                '{', '[' -> bracketCount++
                '}', ']' -> {
                    bracketCount--
                    if (bracketCount == 0) {
                        lastCompleteIndex = i
                    }
                }
            }
        }

        if (lastCompleteIndex > 0) {
            val recoveredText = jsonText.substring(0, lastCompleteIndex + 1)
            return Json.parseToJsonElement(recoveredText).jsonObject
        }

        val trimmed = jsonText.trim()
        if (trimmed.startsWith("{") && !trimmed.endsWith("}")) {
            val recovered = "$trimmed}"
            return try {
                Json.parseToJsonElement(recovered).jsonObject
            } catch (_: Exception) {
                null
            }
        }

    } catch (_: Exception) { }
    return null
}
