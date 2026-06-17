This document is also available in English: [FAQ.md](FAQ.md)

# Preguntas Frecuentes (FAQ)

Preguntas y respuestas comunes sobre ImagePickerKMP.

## Tabla de Contenidos

- [Preguntas Generales](#preguntas-generales)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso e Implementación](#uso-e-implementación)
- [Específico de Plataforma](#específico-de-plataforma)
- [Solución de Problemas](#solución-de-problemas)
- [Rendimiento y Optimización](#rendimiento-y-optimizacion)
- [Personalización](#personalización)

- [Problemas Comunes](#problemas-comunes)

## Preguntas Generales

### ¿Qué es ImagePickerKMP?

ImagePickerKMP es una librería moderna y multiplataforma para selección de imágenes en Kotlin Multiplatform (KMP) que proporciona integración de cámara para Android e iOS.

**Características clave:**
- Integración de cámara multiplataforma
- Manejo inteligente de permisos
- Componentes de UI personalizables

- Captura de fotos de alta calidad
- Manejo de errores completo

### ¿Qué plataformas son compatibles?

- **Android**: API 21+ (Android 5.0+)
- **iOS**: iOS 12.0+
- **Kotlin Multiplatform**: Soporte completo

### ¿Cuáles son los requisitos mínimos?

**Android:**
- SDK mínimo: API 21
- Kotlin: 1.8+
- Compose Multiplatform: 1.4+

**iOS:**
- iOS: 12.0+
- Xcode: 14+
- Kotlin Multiplatform: 1.8+

### ¿La librería es gratuita?

Sí, ImagePickerKMP es open-source y gratuita bajo la licencia MIT. Puedes usarla en proyectos personales y comerciales.

### ¿Cómo se compara con otras librerías de selección de imágenes?

**Ventajas:**
- Multiplataforma con un solo código base
- UI moderna con Compose Multiplatform
- Manejo inteligente de permisos
- Componentes personalizables

- Desarrollo y soporte activo

**Comparado con alternativas:**
- Más moderna que CameraX (solo Android)
- Más integrada que UIImagePickerController (solo iOS)
- Mejor manejo de permisos que la mayoría
- Ventaja multiplataforma sobre soluciones específicas

## Instalación y Configuración

### ¿Cómo agrego ImagePickerKMP a mi proyecto?

Agrega la dependencia en tu `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.22")
}
```

### ¿Qué permisos debo agregar?

**Android** (`AndroidManifest.xml`):
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

**iOS** (`Info.plist`):
```xml
<key>NSCameraUsageDescription</key>
<string>Esta app necesita acceso a la cámara para capturar fotos</string>
```

### ¿Debo configurar algo más?

Para uso básico, no se requiere configuración adicional. La librería maneja la mayoría de la configuración automáticamente.

Para funciones avanzadas, podrías necesitar:
- Configurar temas personalizados

- Añadir diálogos de permisos personalizados
- Configurar preferencias de captura de fotos

### ¿Cómo configuro para desarrollo iOS?

1. **Agrega a tu proyecto iOS**:
   ```ruby
   # Podfile
   target 'YourApp' do
     use_frameworks!
     pod 'ImagePickerKMP', :path => '../ruta/a/tu/libreria'
   end
   ```

2. **Ejecuta pod install**:
   ```bash
   pod install
   ```

3. **Importa en tu código iOS**:
   ```swift
   import ImagePickerKMP
   ```

## Uso e Implementación

### ¿Cuál es la implementación básica?

```kotlin
@Composable
fun MyImagePicker() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Take Photo")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            println("Photo captured: ${photo.uri}")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("User dismissed")
        }
        else -> { /* Idle */ }
    }
}
```

### ¿Cómo manejo los permisos?

La librería maneja los permisos automáticamente, pero puedes personalizar el comportamiento:

```kotlin
@Composable
fun CustomPermissionHandler() {
    RequestCameraPermission(
        titleDialogConfig = "Permiso de cámara requerido",
        descriptionDialogConfig = "Por favor habilita el acceso a la cámara",
        btnDialogConfig = "Abrir ajustes",
        onPermissionPermanentlyDenied = {
            // Manejar denegación permanente
        },
        onResult = { granted ->
            // Manejar resultado de permiso
        }
    )
}
```

### ¿Cómo personalizo la UI?

```kotlin
@Composable
fun CustomImagePicker() {
    val picker = rememberImagePicker(
        cameraCaptureConfig = CameraCaptureConfig(
            preference = CapturePhotoPreference.HIGH_QUALITY,
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customPermissionHandler = { config ->
                    // Manejo personalizado de permisos
                },
                customConfirmationView = { result, onConfirm, onRetry ->
                    // Vista de confirmación personalizada
                }
            )
        )
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            // Manejar foto capturada
        }
        is ImagePickerResult.Error -> {
            // Manejar errores
        }
        is ImagePickerResult.Dismissed -> {
            // Usuario canceló
        }
        else -> { /* Idle */ }
    }
}
```

### ¿Cómo manejo diferentes calidades de foto?

```kotlin
@Composable
fun HighQualityImagePicker() {
    val picker = rememberImagePicker(
        preference = CapturePhotoPreference.HIGH_QUALITY
    )

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar en alta calidad")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            // Manejar foto de alta calidad
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        else -> {}
    }
}
```

## ¿Cómo manejo la cancelación del usuario?

La librería proporciona el estado `ImagePickerResult.Dismissed` que se activa cuando el usuario cancela o cierra el selector sin seleccionar nada. Esto es esencial para resetear el estado de tu UI.

### Ejemplo con Cámara

```kotlin
@Composable
fun MiSelectorImagen() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Tomar Foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val foto = result.photos.first()
            println("Foto capturada: ${foto.uri}")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("Usuario canceló o cerró el selector")
        }
        else -> { /* Idle */ }
    }
}
```

### Ejemplo con Galería

```kotlin
@Composable
fun MiSelectorGaleria() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchGallery(allowMultiple = true) }) {
        Text("Seleccionar de la Galería")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val fotos = result.photos
            println("Seleccionadas ${fotos.size} imágenes")
        }
        is ImagePickerResult.Error -> {
            println("Error: ${result.exception.message}")
        }
        is ImagePickerResult.Dismissed -> {
            println("Usuario canceló la selección de galería")
        }
        else -> { /* Idle */ }
    }
}
```

**Cuándo se activa `ImagePickerResult.Dismissed`:**
- **Android:** Usuario cancela el diálogo de selección o la interfaz de cámara
- **iOS:** Usuario toca "Cancelar" en el diálogo o la interfaz de cámara
- **iOS:** Usuario cancela la solicitud de permisos de cámara
- **iOS:** Usuario cancela la interfaz de cámara (toca "Cancel" o "X")

## Específico de Plataforma

### ¿Hay diferencias entre Android y iOS?

**Similitudes:**
- Misma interfaz de API
- Mismo manejo de permisos
- Mismo manejo de errores
- Mismas opciones de personalización

**Diferencias:**
- Android usa CameraX, iOS usa AVFoundation
- El flujo de permisos es ligeramente diferente (iOS muestra ajustes tras la primera denegación)
- Algunas optimizaciones específicas de plataforma

### ¿Cómo manejo código específico de plataforma?

```kotlin
@Composable
fun PlatformSpecificImagePicker() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            // Manejo agnóstico de plataforma
        }
        is ImagePickerResult.Error -> {
            when (result.exception) {
                is CameraPermissionException -> {
                    // Manejar errores de permisos
                }
                is PhotoCaptureException -> {
                    // Manejar errores de captura
                }
                else -> {
                    // Manejar otros errores
                }
            }
        }
        is ImagePickerResult.Dismissed -> {
            // Usuario canceló
        }
        else -> {}
    }
}
```

### ¿Y las características específicas de iOS?

Las características específicas de iOS son gestionadas internamente por la librería. No necesitas escribir código específico para la mayoría de los casos.

Para funciones avanzadas de iOS:
```kotlin
@Composable
fun IOSImagePicker() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            // Manejo específico de iOS
        }
        is ImagePickerResult.Error -> {
            // Manejo de errores específico de iOS
        }
        else -> {}
    }
}
```

## Solución de Problemas

### La cámara no inicia. ¿Qué debo revisar?

1. **Permisos**: Asegúrate de que el permiso de cámara esté concedido
2. **Hardware**: Verifica que el dispositivo tenga cámara
3. **Ciclo de vida**: Verifica el estado del componente
4. **Dependencias**: Revisa que todas las dependencias estén agregadas

### El diálogo de permisos no aparece. ¿Qué pasa?

1. **Revisa el manifest**: Asegúrate de declarar el permiso de cámara
2. **Revisa Info.plist**: Asegúrate de tener NSCameraUsageDescription (iOS)
3. **Revisa la implementación**: Usa RequestCameraPermission
4. **Revisa la plataforma**: Verifica la configuración específica

### Recibo un error "Camera not available". ¿Por qué?

1. **Hardware**: El dispositivo puede no tener cámara
2. **Permisos**: El permiso puede estar denegado
3. **Cámara en uso**: Otra app puede estar usando la cámara
4. **Simulador**: La cámara no está disponible en simulador (usa un dispositivo)

### La app se cierra al tomar fotos. ¿Cómo lo soluciono?

1. **Memoria**: Usa compresión de imagen para fotos grandes
2. **Ciclo de vida**: Maneja correctamente el ciclo de vida
3. **Manejo de excepciones**: Añade manejo de errores adecuado

```kotlin
@Composable
fun RobustImagePicker() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            val photo = result.photos.first()
            try {
                // Procesar foto de forma segura
                processPhoto(photo)
            } catch (e: Exception) {
                // Manejar errores de procesamiento
                showError("Error al procesar la foto: ${e.message}")
            }
        }
        is ImagePickerResult.Error -> {
            // Manejar errores de captura
            showError("Error de cámara: ${result.exception.message}")
        }
        else -> {}
    }
}
```

### ¿Cómo depuro problemas de permisos?

```kotlin
// Depurar estado de permisos
fun debugPermissions(context: Context) {
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    
    println("Permiso de cámara concedido: $hasPermission")
}
```

### La compilación de iOS falla con error de enlazador: `_OBJC_CLASS_$_CLLocation` o `CoreLocation.framework` no encontrado

**Síntomas:**

```
Could not find or use auto-linked framework '_LocationEssentials': framework '_LocationEssentials' not found
ld: Undefined symbols:
  _OBJC_CLASS_$_CLLocation, referenced from:
       in ComposeApp[...](libImagePickerKMP:library-cache.a.o)
linker command failed with exit code 1
```

Android y JVM Desktop funcionan correctamente, pero la compilación de iOS falla durante la fase de enlazado.

**Causa:**

ImagePickerKMP usa `CoreLocation` internamente (p. ej. para metadatos de ubicación al capturar imágenes). En algunas configuraciones de Xcode / KMP el framework **no se enlaza automáticamente**, por lo que el linker no puede resolver los símbolos de `CLLocation`.

**Solución:**

Añade `CoreLocation.framework` manualmente en las **Build Phases** de tu proyecto Xcode:

1. Abre tu proyecto iOS (`.xcworkspace` o `.xcodeproj`) en **Xcode**.
2. Selecciona el target de tu app en el navegador de proyecto.
3. Ve a **Build Phases → Link Binary With Libraries**.
4. Haz clic en **+** y busca **CoreLocation**.
5. Selecciona `CoreLocation.framework` y haz clic en **Add**.
6. Limpia el build folder (**Product → Clean Build Folder**, o `⇧⌘K`) y vuelve a compilar.

> ✅ No se requieren cambios en el código — es únicamente un paso de configuración a nivel de proyecto.

**Entorno reportado:** Xcode 15.2 · iOS 15 · Kotlin 2.2.x · ImagePickerKMP 1.0.35

## Rendimiento y Optimización

### ¿Cómo optimizo el uso de memoria?

1. **Usa URIs en vez de Bitmaps**:
```kotlin
@Composable
fun MemoryEfficientImagePicker() {
    val picker = rememberImagePicker()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Guarda URI en vez de Bitmap
            imageUri = result.photos.first().uri
        }
        else -> {}
    }

    // Carga la imagen solo cuando sea necesario
    imageUri?.let { uri ->
        AsyncImage(
            model = uri,
            contentDescription = "Captured photo"
        )
    }
}
```

2. **Usa compresión de imagen**:
```kotlin
@Composable
fun CompressedImagePicker() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            // Comprime la imagen antes de procesar
            val compressedImage = compressImage(result.photos.first().uri, 80)
        }
        else -> {}
    }
}
```

### ¿Cómo mejoro el tiempo de inicio de la cámara?

1. **Usa preferencia FAST**:
```kotlin
val picker = rememberImagePicker(
    preference = CapturePhotoPreference.FAST
)

Button(onClick = { picker.launchCamera() }) {
    Text("Captura rápida")
}
```

2. **Preinicializa la cámara**:
```kotlin
// Preinicializa la cámara en background
LaunchedEffect(Unit) {
    initializeCamera()
}
```

### ¿Cómo manejo fotos grandes?

```kotlin
@Composable
fun LargePhotoHandler() {
    val picker = rememberImagePicker()

    Button(onClick = { picker.launchCamera() }) {
        Text("Capturar foto")
    }

    when (val result = picker.result) {
        is ImagePickerResult.Success -> {
            lifecycleScope.launch(Dispatchers.IO) {
                // Procesa la foto grande en background
                val processedImage = processLargeImage(result.photos.first().uri)
                withContext(Dispatchers.Main) {
                    // Actualiza la UI con la imagen procesada
                }
            }
        }
        else -> {}
    }
}
```

## Personalización

### ¿Cómo creo diálogos de permisos personalizados?

```kotlin
@Composable
fun CustomPermissionDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
```

### ¿Cómo creo vistas de confirmación personalizadas?

```kotlin
@Composable
fun CustomConfirmationView(
    result: PhotoResult,
    onConfirm: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Vista previa de la foto
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier.weight(1f)
        )
        
        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
            Button(onClick = onConfirm) {
                Text("Use Photo")
            }
        }
    }
}
```

### ¿Cómo aplico temas personalizados?

```kotlin
@Composable
fun ThemedImagePicker() {
    val customTheme = remember {
        MaterialTheme.colors.copy(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFF42A5F5)
        )
    }

    MaterialTheme(colors = customTheme) {
        val picker = rememberImagePicker()

        Button(onClick = { picker.launchCamera() }) {
            Text("Capturar foto")
        }

        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                val photo = result.photos.first()
                // Manejar captura de foto
            }
            is ImagePickerResult.Error -> {
                // Manejar errores
            }
            else -> {}
        }
    }
}
```

## Problemas Comunes

### ¿Por qué las fotos de la cámara frontal se ven "espejadas" o rotadas?

**Problema**: Las fotos capturadas con la cámara frontal aparecen con orientación incorrecta (espejadas o rotadas).

**Causa**: Las cámaras frontales de Android tienen una orientación diferente a las traseras. La imagen se captura con una orientación que no es natural para el usuario.

**Solución**: La librería ahora incluye corrección automática de orientación para fotos de cámara frontal. El sistema:

1. **Detecta automáticamente** si la foto fue tomada con cámara frontal
2. **Aplica corrección de espejo** solo cuando es necesario
3. **Mantiene la calidad** de la imagen original
4. **Es eficiente** - solo procesa cuando realmente necesita corrección

**Ejemplo de uso**:
```kotlin
val picker = rememberImagePicker()

Button(onClick = { picker.launchCamera() }) {
    Text("Capturar foto")
}

when (val result = picker.result) {
    is ImagePickerResult.Success -> {
        val photo = result.photos.first()
        // La imagen ya viene corregida automáticamente
        // No necesitas hacer nada adicional
    }
    else -> {}
}
```

**Nota**: Esta corrección se aplica automáticamente y es transparente para el desarrollador. No necesitas configurar nada adicional.

### ¿Cómo funciona la corrección de orientación?

La corrección incluye:

1. **Lectura de metadatos EXIF**: Se lee la orientación original de la imagen
2. **Aplicación de rotación**: Se corrige la rotación basada en los metadatos
3. **Corrección de espejo**: Solo para cámara frontal, se aplica un espejo horizontal
4. **Optimización**: Solo se procesa si realmente es necesario

### ¿Afecta el rendimiento?

No, la corrección está optimizada para:

- **Procesamiento solo cuando es necesario**: Si no hay corrección requerida, se devuelve la imagen original
- **Gestión eficiente de memoria**: Los bitmaps se reciclan correctamente
- **Procesamiento asíncrono**: No bloquea la UI

### ¿Puedo desactivar la corrección automática?

Actualmente la corrección es automática y no se puede desactivar, ya que mejora significativamente la experiencia del usuario. Si necesitas el comportamiento original, puedes procesar la imagen manualmente después de recibirla.

## Preguntas Adicionales

### ¿Dónde puedo obtener ayuda?

- **Documentación**: [README.es.md](../README.es.md)
- **Referencia de API**: [API_REFERENCE.es.md](API_REFERENCE.es.md)
- **Ejemplos**: [EXAMPLES.es.md](EXAMPLES.es.md)
- **GitHub Issues**: [GitHub Issues](https://github.com/ismoy/ImagePickerKMP/issues)
- **Discusiones**: [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- **Email**: belizairesmoy72@gmail.com

### ¿Cómo reporto un bug?

1. **Busca issues existentes**: Verifica si ya fue reportado
2. **Crea un nuevo issue**: Usa la plantilla de bug report
3. **Proporciona detalles**: Incluye pasos para reproducir, info de entorno, logs
4. **Da seguimiento**: Responde a preguntas de los maintainers

### ¿Cómo solicito una característica?

1. **Busca issues existentes**: Verifica si ya fue solicitada
2. **Crea una solicitud**: Usa la plantilla de feature request
3. **Proporciona detalles**: Incluye caso de uso, implementación propuesta
4. **Discute**: Participa en la comunidad

### ¿Cómo contribuyo?

1. **Haz fork del repositorio**
2. **Crea una rama de feature**
3. **Haz tus cambios**
4. **Añade tests**
5. **Envía un pull request**

Consulta [CONTRIBUTING.es.md](CONTRIBUTING.es.md) para guías detalladas.

---

**¿Aún tienes preguntas?** No dudes en preguntar en nuestras [GitHub Discussions](https://github.com/ismoy/ImagePickerKMP/discussions) o contáctanos directamente.
