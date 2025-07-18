# Guía de Internacionalización (i18n) - ImagePickerKMP

## Resumen

Esta implementación añade un sistema de internacionalización manual para la librería BelZSpeedScan, permitiendo a los desarrolladores mostrar textos en el idioma del dispositivo sin depender de librerías externas.

## Características principales

- Sistema completamente manual (sin dependencias externas)
- Soporte para múltiples idiomas (inglés, español, francés, etc.)
- Type-safe con enum class para evitar errores
- Implementado para Android e iOS
- Compatible con el patrón expect/actual de Kotlin Multiplatform

## Estructura del sistema

### Archivos principales

#### 1. API Común (commonMain)
- **Strings.kt**: Define el enum `StringResource` y las funciones `expect`

#### 2. Implementación Android (androidMain)
- **Strings.kt**: Implementación `actual` para Android
- **res/values/strings.xml**: Strings en inglés
- **res/values-es/strings.xml**: Strings en español
- **res/values-fr/strings.xml**: Strings en francés

#### 3. Implementación iOS (iosMain)
- **Strings.kt**: Implementación `actual` para iOS
- **resources/en.lproj/Localizable.strings**: Strings en inglés
- **resources/es.lproj/Localizable.strings**: Strings en español
- **resources/fr.lproj/Localizable.strings**: Strings en francés

## Strings disponibles

### Permisos de cámara
```kotlin
StringResource.CAMERA_PERMISSION_REQUIRED
StringResource.CAMERA_PERMISSION_DESCRIPTION
StringResource.OPEN_SETTINGS
StringResource.CAMERA_PERMISSION_DENIED
StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION
StringResource.GRANT_PERMISSION
StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED
```

### Confirmación de imagen
```kotlin
StringResource.IMAGE_CONFIRMATION_TITLE
StringResource.ACCEPT_BUTTON
StringResource.RETRY_BUTTON
```

### Diálogos de selección
```kotlin
StringResource.SELECT_OPTION_DIALOG_TITLE
StringResource.TAKE_PHOTO_OPTION
StringResource.SELECT_FROM_GALLERY_OPTION
StringResource.CANCEL_OPTION
```

### Accesibilidad
```kotlin
StringResource.PREVIEW_IMAGE_DESCRIPTION
StringResource.HD_QUALITY_DESCRIPTION
StringResource.SD_QUALITY_DESCRIPTION
```

### Errores
```kotlin
StringResource.INVALID_CONTEXT_ERROR
StringResource.PHOTO_CAPTURE_ERROR
StringResource.GALLERY_SELECTION_ERROR
StringResource.PERMISSION_ERROR
```

## Uso básico

### En Composables
```kotlin
@Composable
fun MyComponent() {
    Text(
        text = stringResource(StringResource.IMAGE_CONFIRMATION_TITLE)
    )
}
```

### Fuera de Composables
```kotlin
fun myFunction() {
    val errorMessage = getStringResource(StringResource.PHOTO_CAPTURE_ERROR)
    // Usar el mensaje...
}
```

## Configuración de permisos con traducciones

### Opción 1: Usar valores por defecto (inglés)
```kotlin
val config = PermissionConfig() // Usa strings en inglés
```

### Opción 2: Usar traducciones fuera de Composables
```kotlin
val config = PermissionConfig.createLocalized() // Usa traducciones según el idioma del dispositivo
```

### Opción 3: Usar traducciones dentro de Composables (recomendado)
```kotlin
@Composable
fun MyPermissionHandler() {
    val config = PermissionConfig.createLocalizedComposable() // Usa traducciones con contexto de Compose
    
    RequestCameraPermission(
        titleDialogConfig = config.titleDialogConfig,
        descriptionDialogConfig = config.descriptionDialogConfig,
        btnDialogConfig = config.btnDialogConfig,
        titleDialogDenied = config.titleDialogDenied,
        descriptionDialogDenied = config.descriptionDialogDenied,
        btnDialogDenied = config.btnDialogDenied,
        onPermissionPermanentlyDenied = { /* ... */ },
        onResult = { granted -> /* ... */ }
    )
}
```

### ⚡ Automático por defecto

**¡Importante!** La librería ahora usa automáticamente traducciones por defecto en todos los componentes internos:

- `CameraCaptureView` usa `PermissionConfig.createLocalizedComposable()` automáticamente
- `ImagePickerLauncher` (iOS) usa `PermissionConfig.createLocalizedComposable()` automáticamente
- Los diálogos de permisos aparecerán en el idioma del dispositivo sin configuración adicional

## Configuración automática

La librería ahora usa automáticamente los strings localizados según el idioma del dispositivo:

### Android
- Lee desde `res/values/strings.xml` (inglés por defecto)
- Lee desde `res/values-es/strings.xml` (español)
- Lee desde `res/values-fr/strings.xml` (francés)
- Añade más idiomas creando `res/values-{código}/strings.xml`

### iOS
- Lee desde `en.lproj/Localizable.strings` (inglés por defecto)
- Lee desde `es.lproj/Localizable.strings` (español)
- Lee desde `fr.lproj/Localizable.strings` (francés)
- Añade más idiomas creando `{código}.lproj/Localizable.strings`

## Añadir nuevos idiomas

### Para Android
1. Crea `res/values-{código}/strings.xml`
2. Traduce todos los strings

Ejemplo para francés (`res/values-fr/strings.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="camera_permission_required">Permission d\'appareil photo requise</string>
    <string name="image_confirmation_title">Confirmer l\'image</string>
    <!-- ... más traducciones -->
</resources>
```

### Para iOS
**⚠️ IMPORTANTE:** Los archivos de recursos de la librería no se incluyen automáticamente en el bundle de la app iOS. Debes añadir los archivos de localización a tu proyecto iOS.

1. Crea `{código}.lproj/Localizable.strings` en tu proyecto iOS
2. Traduce todos los strings
3. Asegúrate de que los archivos están incluidos en el target de tu app

Ejemplo para francés (`fr.lproj/Localizable.strings`):
```
"camera_permission_required" = "Permission d'appareil photo requise";
"image_confirmation_title" = "Confirmer l'image";
/* ... más traducciones */
```

**Ver [Guía de Localización para iOS](docs/IOS_LOCALIZATION_GUIDE.md) para instrucciones detalladas.**

## Añadir nuevos strings

### 1. Actualizar el enum
En `commonMain/kotlin/io/github/ismoy/imagepickerkmp/Strings.kt`:
```kotlin
enum class StringResource {
    // ... strings existentes ...
    NEW_STRING_ID,
}
```

### 2. Actualizar implementación Android
En `androidMain/kotlin/io/github/ismoy/imagepickerkmp/Strings.kt`:
```kotlin
val resourceId = when (id) {
    // ... casos existentes ...
    StringResource.NEW_STRING_ID -> R.string.new_string_id
}
```

### 3. Actualizar implementación iOS
En `iosMain/kotlin/io/github/ismoy/imagepickerkmp/Strings.kt`:
```kotlin
val key = when (id) {
    // ... casos existentes ...
    StringResource.NEW_STRING_ID -> "new_string_id"
}
```

### 4. Añadir strings a los archivos de recursos
- **Android**: Añadir a todos los archivos `strings.xml`
- **iOS**: Añadir a todos los archivos `Localizable.strings`

## Migración desde el sistema anterior

### Antes (hardcoded strings)
```kotlin
Text(text = "Are you satisfied with the photo?")
```

### Después (i18n)
```kotlin
Text(text = stringResource(StringResource.IMAGE_CONFIRMATION_TITLE))
```

### Antes (constantes)
```kotlin
const val TITLE_DIALOG_CONFIG = "Camera permission required"
```

### Después (i18n)
```kotlin
// Usar directamente
stringResource(StringResource.CAMERA_PERMISSION_REQUIRED)
```

## Ventajas del nuevo sistema

1. **Type Safety**: El enum previene errores de tipeo
2. **Centralizado**: Todos los strings en un lugar
3. **Automático**: Detección automática del idioma del dispositivo
4. **Extensible**: Fácil añadir nuevos idiomas
5. **Sin dependencias**: No requiere librerías externas
6. **Compatible**: Funciona con el patrón expect/actual de KMP

## Troubleshooting

### Problema: Los textos aparecen en inglés aunque el dispositivo esté en español

**Solución:**
1. Verifica que los archivos de recursos están en la ubicación correcta
2. En iOS, asegúrate de que los archivos están incluidos en el bundle de la app
3. Limpia y recompila el proyecto

### Problema: Error de compilación con PermissionConfig

**Solución:**
- Usa `PermissionConfig.createLocalizedComposable()` dentro de Composables
- Usa `PermissionConfig.createLocalized()` fuera de Composables
- O usa `PermissionConfig()` para valores por defecto en inglés

### Problema: Los diálogos de permisos aparecen en inglés

**Solución:**
- La librería ahora usa traducciones automáticamente por defecto
- Si sigues viendo textos en inglés, verifica que los archivos de recursos estén en la ubicación correcta
- En iOS, asegúrate de incluir los archivos de recursos en el bundle de la app

### Problema: Los textos no se actualizan al cambiar el idioma

**Solución:**
- En Android, reinicia la app después de cambiar el idioma
- En iOS, asegúrate de que los archivos de recursos están actualizados 