package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.localeIdentifier
import platform.Foundation.preferredLanguages
import platform.Foundation.stringWithContentsOfFile

/**
 * Implementación actual para iOS de la función stringResource.
 * Lee los strings desde los archivos Localizable.strings según el idioma del dispositivo.
 */
@Composable
internal actual fun stringResource(id: StringResource): String {
    val key = when (id) {
        // Permisos de cámara
        StringResource.CAMERA_PERMISSION_REQUIRED -> "camera_permission_required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "camera_permission_description"
        StringResource.OPEN_SETTINGS -> "open_settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "camera_permission_denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "camera_permission_denied_description"
        StringResource.GRANT_PERMISSION -> "grant_permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "camera_permission_permanently_denied"
        
        // Confirmación de imagen
        StringResource.IMAGE_CONFIRMATION_TITLE -> "image_confirmation_title"
        StringResource.ACCEPT_BUTTON -> "accept_button"
        StringResource.RETRY_BUTTON -> "retry_button"
        
        // Diálogos de selección
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "select_option_dialog_title"
        StringResource.TAKE_PHOTO_OPTION -> "take_photo_option"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "select_from_gallery_option"
        StringResource.CANCEL_OPTION -> "cancel_option"
        
        // Accesibilidad
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "preview_image_description"
        StringResource.HD_QUALITY_DESCRIPTION -> "hd_quality_description"
        StringResource.SD_QUALITY_DESCRIPTION -> "sd_quality_description"
        
        // Errores
        StringResource.INVALID_CONTEXT_ERROR -> "invalid_context_error"
        StringResource.PHOTO_CAPTURE_ERROR -> "photo_capture_error"
        StringResource.GALLERY_SELECTION_ERROR -> "gallery_selection_error"
        StringResource.PERMISSION_ERROR -> "permission_error"
    }
    
    // Debug: Verificar idioma del dispositivo
    debugLanguageDetection()
    
    // Usar la función de carga manual
    val localizedString = loadLocalizedString(key)
    return if (localizedString == key) {
        // Si retorna la misma clave, significa que no encontró la traducción
        // Usamos valores por defecto en inglés
        getDefaultString(id)
    } else {
        localizedString
    }
}

/**
 * Función de debug para verificar la detección del idioma
 */
@OptIn(ExperimentalForeignApi::class)
private fun debugLanguageDetection() {
    try {
        val currentLocale = NSLocale.currentLocale
        val languageCode = currentLocale.languageCode
        val countryCode = currentLocale.countryCode
        val localeIdentifier = currentLocale.localeIdentifier
        
        println("🔍 iOS Language Debug:")
        println("   Language Code: $languageCode")
        println("   Country Code: $countryCode")
        println("   Locale Identifier: $localeIdentifier")
        
        // Verificar si los archivos de localización están disponibles
        val bundle = NSBundle.mainBundle
        val bundlePath = bundle.bundlePath
        println("   Bundle Path: $bundlePath")
        
        // Verificar archivos de localización disponibles
        val localizations = bundle.localizations
        println("   Available Localizations: ${localizations.joinToString(", ")}")
        
        // Verificar el idioma preferido del sistema
        val preferredLanguages = NSLocale.preferredLanguages
        println("   Preferred Languages: ${preferredLanguages.joinToString(", ")}")
        
        // Verificar archivos .lproj manualmente
        val fileManager = platform.Foundation.NSFileManager.defaultManager
        val bundleContents = fileManager.contentsOfDirectoryAtPath(bundlePath, null)
        val lprojDirectories = bundleContents?.filter { it.toString().endsWith(".lproj") }
        println("   .lproj directories found: ${lprojDirectories?.joinToString(", ") ?: "none"}")
        
        // Intentar cargar un string de prueba
        val testKey = "camera_permission_required"
        val testString = bundle.localizedStringForKey(testKey, value = null, table = null)
        println("   Test String for '$testKey': '$testString'")
        
        // Verificar si el string se encontró o se usó el valor por defecto
        if (testString == testKey) {
            println("   ⚠️  String not found in localization files, using default")
        } else {
            println("   ✅ String found in localization files")
        }
        
        // Probar carga manual
        println("   Testing manual loading:")
        listOf("es", "fr", "en").forEach { lang ->
            val localizedBundle = NSBundle.bundleWithPath("$bundlePath/$lang.lproj")
            if (localizedBundle != null) {
                val manualString = localizedBundle.localizedStringForKey(testKey, value = null, table = null)
                println("     $lang.lproj: ${if (manualString != testKey) "✅ '$manualString'" else "❌ not found"}")
            } else {
                println("     $lang.lproj: ❌ directory not found")
            }
        }
        
        // Probar lectura directa de archivos
        println("   Testing direct file reading:")
        listOf("es", "fr", "en").forEach { lang ->
            val directString = readLocalizedStringFromFile(testKey, lang)
            if (directString != null) {
                println("     $lang.lproj: ✅ '$directString'")
            } else {
                println("     $lang.lproj: ❌ file not found or parsing failed")
            }
        }
        
        // Probar traducciones hardcodeadas
        println("   Testing hardcoded translations:")
        listOf("es", "fr", "en").forEach { lang ->
            val hardcodedString = getHardcodedLocalization(testKey, lang)
            if (hardcodedString != null) {
                println("     $lang hardcoded: ✅ '$hardcodedString'")
            } else {
                println("     $lang hardcoded: ❌ not available")
            }
        }
        
    } catch (e: Exception) {
        println("❌ Error en debug de idioma: ${e.message}")
    }
}

/**
 * Función auxiliar para obtener strings por defecto en inglés
 */
private fun getDefaultString(id: StringResource): String {
    return when (id) {
        // Permisos de cámara
        StringResource.CAMERA_PERMISSION_REQUIRED -> "Camera permission required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "Camera permission is required to capture photos. Please grant it in settings"
        StringResource.OPEN_SETTINGS -> "Open settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "Camera permission denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "Camera permission is required to capture photos. Please grant the permissions"
        StringResource.GRANT_PERMISSION -> "Grant permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "Camera permission permanently denied"
        
        // Confirmación de imagen
        StringResource.IMAGE_CONFIRMATION_TITLE -> "Are you satisfied with the photo?"
        StringResource.ACCEPT_BUTTON -> "Accept"
        StringResource.RETRY_BUTTON -> "Retry"
        
        // Diálogos de selección
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "Select option"
        StringResource.TAKE_PHOTO_OPTION -> "Take photo"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "Select from gallery"
        StringResource.CANCEL_OPTION -> "Cancel"
        
        // Accesibilidad
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "Preview"
        StringResource.HD_QUALITY_DESCRIPTION -> "HD"
        StringResource.SD_QUALITY_DESCRIPTION -> "SD"
        
        // Errores
        StringResource.INVALID_CONTEXT_ERROR -> "Invalid context. Must be ComponentActivity"
        StringResource.PHOTO_CAPTURE_ERROR -> "Photo capture failed"
        StringResource.GALLERY_SELECTION_ERROR -> "Gallery selection failed"
        StringResource.PERMISSION_ERROR -> "Permission error occurred"
    }
}

/**
 * Implementación actual para iOS de la función getStringResource.
 * Versión no-Composable para casos donde no se tiene acceso al contexto de Compose.
 */
internal actual fun getStringResource(id: StringResource): String {
    val key = when (id) {
        // Permisos de cámara
        StringResource.CAMERA_PERMISSION_REQUIRED -> "camera_permission_required"
        StringResource.CAMERA_PERMISSION_DESCRIPTION -> "camera_permission_description"
        StringResource.OPEN_SETTINGS -> "open_settings"
        StringResource.CAMERA_PERMISSION_DENIED -> "camera_permission_denied"
        StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION -> "camera_permission_denied_description"
        StringResource.GRANT_PERMISSION -> "grant_permission"
        StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED -> "camera_permission_permanently_denied"
        
        // Confirmación de imagen
        StringResource.IMAGE_CONFIRMATION_TITLE -> "image_confirmation_title"
        StringResource.ACCEPT_BUTTON -> "accept_button"
        StringResource.RETRY_BUTTON -> "retry_button"
        
        // Diálogos de selección
        StringResource.SELECT_OPTION_DIALOG_TITLE -> "select_option_dialog_title"
        StringResource.TAKE_PHOTO_OPTION -> "take_photo_option"
        StringResource.SELECT_FROM_GALLERY_OPTION -> "select_from_gallery_option"
        StringResource.CANCEL_OPTION -> "cancel_option"
        
        // Accesibilidad
        StringResource.PREVIEW_IMAGE_DESCRIPTION -> "preview_image_description"
        StringResource.HD_QUALITY_DESCRIPTION -> "hd_quality_description"
        StringResource.SD_QUALITY_DESCRIPTION -> "sd_quality_description"
        
        // Errores
        StringResource.INVALID_CONTEXT_ERROR -> "invalid_context_error"
        StringResource.PHOTO_CAPTURE_ERROR -> "photo_capture_error"
        StringResource.GALLERY_SELECTION_ERROR -> "gallery_selection_error"
        StringResource.PERMISSION_ERROR -> "permission_error"
    }
    
    // Debug: Verificar idioma del dispositivo
    debugLanguageDetection()
    
    // Usar la función de carga manual
    val localizedString = loadLocalizedString(key)
    return if (localizedString == key) {
        // Si retorna la misma clave, significa que no encontró la traducción
        // Usamos valores por defecto en inglés
        getDefaultString(id)
    } else {
        localizedString
    }
}

/**
 * Función de prueba para verificar manualmente la localización
 * Esta función puede ser llamada desde el código para debug
 */
internal fun testLocalization() {
    println("🧪 Testing iOS Localization...")
    
    val bundle = NSBundle.mainBundle
    val currentLocale = NSLocale.currentLocale
    
    println("Current Locale: ${currentLocale.localeIdentifier}")
    println("Bundle Localizations: ${bundle.localizations.joinToString(", ")}")
    println("Preferred Languages: ${NSLocale.preferredLanguages.joinToString(", ")}")
    
    // Probar diferentes claves
    val testKeys = listOf(
        "camera_permission_required",
        "open_settings",
        "take_photo_option"
    )
    
    testKeys.forEach { key ->
        val localizedString = bundle.localizedStringForKey(key, value = null, table = null)
        val isFound = localizedString != key
        val status = if (isFound) "✅" else "❌"
        println("$status '$key' -> '$localizedString'")
    }
    
    // Probar con diferentes idiomas
    println("\nTesting with different language codes:")
    listOf("en", "es", "fr").forEach { languageCode ->
        val testKey = "camera_permission_required"
        val localizedString = bundle.localizedStringForKey(testKey, value = null, table = null)
        println("Language '$languageCode': '$localizedString'")
    }
}

/**
 * Función para cargar manualmente los archivos de localización
 */
private fun loadLocalizedString(key: String, languageCode: String? = null): String {
    // Si se especifica un idioma específico, intentar cargar desde ese archivo
    if (languageCode != null) {
        val localizedString = readLocalizedStringFromFile(key, languageCode)
        if (localizedString != null) {
            return localizedString
        }
        // Fallback a traducciones hardcodeadas
        val hardcodedString = getHardcodedLocalization(key, languageCode)
        if (hardcodedString != null) {
            return hardcodedString
        }
    }
    
    // Intentar con el idioma del sistema
    val currentLocale = NSLocale.currentLocale
    val systemLanguage = currentLocale.languageCode
    
    if (systemLanguage != "en") {
        val localizedString = readLocalizedStringFromFile(key, systemLanguage)
        if (localizedString != null) {
            return localizedString
        }
        // Fallback a traducciones hardcodeadas
        val hardcodedString = getHardcodedLocalization(key, systemLanguage)
        if (hardcodedString != null) {
            return hardcodedString
        }
    }
    
    // Intentar con los idiomas preferidos del sistema
    val preferredLanguages = NSLocale.preferredLanguages
    for (preferredLanguage in preferredLanguages) {
        val langCode = preferredLanguage.toString().split('-').firstOrNull()
        if (langCode != null && langCode != "en") {
            val localizedString = readLocalizedStringFromFile(key, langCode)
            if (localizedString != null) {
                return localizedString
            }
            // Fallback a traducciones hardcodeadas
            val hardcodedString = getHardcodedLocalization(key, langCode)
            if (hardcodedString != null) {
                return hardcodedString
            }
        }
    }
    
    // Si no se encuentra, usar inglés como fallback por defecto
    // Primero intentar cargar desde archivo inglés
    val englishString = readLocalizedStringFromFile(key, "en")
    if (englishString != null) {
        return englishString
    }
    
    // Si no hay archivo inglés, usar traducciones hardcodeadas en inglés
    val englishHardcoded = getHardcodedLocalization(key, "en")
    if (englishHardcoded != null) {
        return englishHardcoded
    }
    
    // Último recurso: usar el método estándar de iOS
    val bundle = NSBundle.mainBundle
    return bundle.localizedStringForKey(key, value = null, table = null)
}

/**
 * Función para leer directamente los archivos de localización
 */
@OptIn(ExperimentalForeignApi::class)
private fun readLocalizedStringFromFile(key: String, languageCode: String): String? {
    return try {
        val bundle = NSBundle.mainBundle
        val bundlePath = bundle.bundlePath
        val lprojPath = "$bundlePath/$languageCode.lproj"
        val stringsPath = "$lprojPath/Localizable.strings"
        
        val fileManager = platform.Foundation.NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(stringsPath)) {
            return null
        }
        
        val fileContent = platform.Foundation.NSString.stringWithContentsOfFile(
            stringsPath,
            platform.Foundation.NSUTF8StringEncoding,
            null
        )
        
        if (fileContent != null) {
            // Parsear el archivo .strings manualmente
            val contentString = fileContent.toString()
            val lines = contentString.split('\n')
            for (line in lines) {
                val trimmedLine = line.trim()
                if (trimmedLine.startsWith("\"$key\"") && trimmedLine.contains("=")) {
                    val parts = trimmedLine.split('=').map { it.trim() }
                    if (parts.size >= 2) {
                        var value = parts[1]
                        if (value.endsWith(";")) value = value.dropLast(1)
                        value = value.trim()
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length - 1)
                        }
                        return value
                    }
                }
            }
        }
        
        null
    } catch (e: Exception) {
        println("Error reading localization file: ${e.message}")
        null
    }
}

/**
 * Función para obtener traducciones hardcodeadas como fallback
 */
private fun getHardcodedLocalization(key: String, languageCode: String): String? {
    val translations = when (languageCode) {
        "en" -> mapOf(
            "camera_permission_required" to "Camera permission required",
            "camera_permission_description" to "Camera permission is required to capture photos. Please grant it in settings",
            "open_settings" to "Open settings",
            "camera_permission_denied" to "Camera permission denied",
            "camera_permission_denied_description" to "Camera permission is required to capture photos. Please grant the permissions",
            "grant_permission" to "Grant permission",
            "camera_permission_permanently_denied" to "Camera permission permanently denied",
            "image_confirmation_title" to "Are you satisfied with the photo?",
            "accept_button" to "Accept",
            "retry_button" to "Retry",
            "select_option_dialog_title" to "Select option",
            "take_photo_option" to "Take photo",
            "select_from_gallery_option" to "Select from gallery",
            "cancel_option" to "Cancel",
            "preview_image_description" to "Preview",
            "hd_quality_description" to "HD",
            "sd_quality_description" to "SD",
            "invalid_context_error" to "Invalid context. Must be ComponentActivity",
            "photo_capture_error" to "Photo capture failed",
            "gallery_selection_error" to "Gallery selection failed",
            "permission_error" to "Permission error occurred"
        )
        "es" -> mapOf(
            "camera_permission_required" to "Permiso de cámara requerido",
            "camera_permission_description" to "El permiso de cámara es necesario para capturar fotos. Por favor, concédelo en la configuración",
            "open_settings" to "Abrir configuración",
            "camera_permission_denied" to "Permiso de cámara denegado",
            "camera_permission_denied_description" to "El permiso de cámara es necesario para capturar fotos. Por favor, concede los permisos",
            "grant_permission" to "Conceder permiso",
            "camera_permission_permanently_denied" to "Permiso de cámara denegado permanentemente",
            "image_confirmation_title" to "¿Estás satisfecho con la foto?",
            "accept_button" to "Aceptar",
            "retry_button" to "Reintentar",
            "select_option_dialog_title" to "Seleccionar opción",
            "take_photo_option" to "Tomar foto",
            "select_from_gallery_option" to "Seleccionar de galería",
            "cancel_option" to "Cancelar",
            "preview_image_description" to "Vista previa",
            "hd_quality_description" to "HD",
            "sd_quality_description" to "SD",
            "invalid_context_error" to "Contexto inválido. Debe ser ComponentActivity",
            "photo_capture_error" to "Error al capturar la foto",
            "gallery_selection_error" to "Error al seleccionar de la galería",
            "permission_error" to "Error de permisos"
        )
        "fr" -> mapOf(
            "camera_permission_required" to "Permission d'appareil photo requise",
            "camera_permission_description" to "Cette application a besoin d'accéder à votre appareil photo pour prendre des photos",
            "open_settings" to "Ouvrir les paramètres",
            "camera_permission_denied" to "Permission d'appareil photo refusée",
            "camera_permission_denied_description" to "Pour utiliser cette fonctionnalité, vous devez autoriser l'accès à l'appareil photo dans les paramètres",
            "grant_permission" to "Accorder la permission",
            "camera_permission_permanently_denied" to "Permission d'appareil photo définitivement refusée",
            "image_confirmation_title" to "Confirmer l'image",
            "accept_button" to "Accepter",
            "retry_button" to "Réessayer",
            "select_option_dialog_title" to "Sélectionner une option",
            "take_photo_option" to "Prendre une photo",
            "select_from_gallery_option" to "Sélectionner depuis la galerie",
            "cancel_option" to "Annuler",
            "preview_image_description" to "Aperçu de l'image capturée",
            "hd_quality_description" to "Qualité haute définition",
            "sd_quality_description" to "Qualité standard",
            "invalid_context_error" to "Contexte invalide",
            "photo_capture_error" to "Erreur lors de la capture de la photo",
            "gallery_selection_error" to "Erreur lors de la sélection depuis la galerie",
            "permission_error" to "Erreur de permission"
        )
        else -> null
    }
    
    return translations?.get(key)
}