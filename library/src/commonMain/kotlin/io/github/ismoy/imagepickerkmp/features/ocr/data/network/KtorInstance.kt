package io.github.ismoy.imagepickerkmp.features.ocr.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.DefaultRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import kotlinx.serialization.json.Json


internal object KtorInstance {
    
   
    val client = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = if (DefaultLogger.debugMode) LogLevel.INFO else LogLevel.NONE
        }
        
        install(ContentNegotiation) {
            json(Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 10 * 60 * 1000L
            connectTimeoutMillis = 60 * 1000L
            socketTimeoutMillis = 10 * 60 * 1000L
        }
        
        engine {
            pipelining = false
        }
        
        expectSuccess = false
        
        install(DefaultRequest) {
            headers.append(HttpHeaders.Accept, "application/json")
            headers.append(HttpHeaders.UserAgent, "ImagePickerKMP/1.0")
            headers.append(HttpHeaders.AcceptEncoding, "identity")
            headers.append(HttpHeaders.Connection, "close")
            headers.append(HttpHeaders.CacheControl, "no-cache")
        }
    }
    
    val geminiClient = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = if (DefaultLogger.debugMode) LogLevel.INFO else LogLevel.NONE
        }
        
        install(ContentNegotiation) {
            json(Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 15 * 60 * 1000L
            connectTimeoutMillis = 90 * 1000L
            socketTimeoutMillis = 15 * 60 * 1000L
        }
        
        engine {
            pipelining = false
        }
        
        expectSuccess = false
        
        install(DefaultRequest) {
            headers.append(HttpHeaders.Accept, "application/json")
            headers.append(HttpHeaders.UserAgent, "ImagePickerKMP-Gemini/1.0")
            headers.append(HttpHeaders.AcceptEncoding, "identity")
            headers.append(HttpHeaders.Connection, "close")
            headers.append(HttpHeaders.CacheControl, "no-cache")
        }
    }

}
