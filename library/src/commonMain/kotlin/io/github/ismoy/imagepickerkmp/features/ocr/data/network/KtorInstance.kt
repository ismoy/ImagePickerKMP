package io.github.ismoy.imagepickerkmp.features.ocr.data.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.DefaultRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Singleton object that provides configured Ktor HTTP clients for different OCR services.
 * This centralized approach ensures consistent configuration, resource efficiency, 
 * and better maintainability across all OCR providers.
 * 
 * Benefits:
 * - Resource efficiency: Reuses HTTP client instances instead of creating new ones
 * - Consistency: All providers share the same base configuration
 * - Maintainability: Centralized configuration management
 * - Performance: Better connection pooling and memory usage
 */
object KtorInstance {
    
    /**
     * Standard HTTP client with basic configuration
     */
    val client = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
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
    /**
     * Specialized client for Gemini API requests with enhanced timeout settings
     */
    val geminiClient = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
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
