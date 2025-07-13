This document is also available in English: [INTEGRATION_GUIDE.md](docs/INTEGRATION_GUIDE.md)

# Guía de Integración

Esta guía te ayudará a integrar ImagePickerKMP en tu proyecto Kotlin Multiplatform para plataformas Android e iOS.

## Tabla de Contenidos

- [Prerrequisitos](#prerrequisitos)
- [Configuración en Android](#configuración-en-android)
- [Configuración en iOS](#configuración-en-ios)
- [Integración Básica](#integración-básica)
- [Configuración Avanzada](#configuración-avanzada)
- [Solución de Problemas](#solución-de-problemas)
- [Migración desde otras librerías](#migración-desde-otras-librerías)
- [Selección de Galería y Diálogos en iOS](#selección-de-galería-y-diálogos-en-ios)
- [Ejemplos Específicos por Plataforma](#ejemplos-específicos-por-plataforma)

## Prerrequisitos

Antes de integrar ImagePickerKMP, asegúrate de tener:

- **Proyecto Kotlin Multiplatform** configurado
- **Android Studio** o **IntelliJ IDEA** con plugin de Kotlin
- **Xcode** (para desarrollo iOS)
- **Versiones mínimas de SDK**:
  - Android: API 21+
  - iOS: iOS 12.0+

## Configuración en Android

### 1. Añadir dependencias

En tu `build.gradle.kts` (nivel app):

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
}
```

### 2. Añadir permisos

En tu `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

### 3. Configurar ProGuard (si usas)

Agrega a tu `proguard-rules.pro`:

```proguard
-keep class io.github.ismoy.imagepickerkmp.** { *; }
```

## Configuración en iOS

### 1. Añadir dependencias

En tu `build.gradle.kts` (módulo compartido):

```kotlin
kotlin {
    ios {
        binaries {
            framework {
                baseName = "ImagePickerKMP"
            }
        }
    }
}
```

### 2. Añadir permiso de cámara

En tu `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>Esta app necesita acceso a la cámara para capturar fotos</string>
```

### 3. Configurar Podfile (si usas CocoaPods)

```ruby
target 'YourApp' do
  use_frameworks!
  pod 'ImagePickerKMP', :path => '../ruta/a/tu/libreria'
end
```

## Integración Básica

### Implementación simple

```kotlin
@Composable
fun SimpleImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Manejar captura exitosa
                println("Photo captured: ${result.uri}")
                showPicker = false
            },
            onError = { exception ->
                // Manejar errores
                println("Error: ${exception.message}")
                showPicker = false
            }
        )
    }
    
    Button(onClick = { showPicker = true }) {
        Text("Take Photo")
    }
}
```

### Con configuración personalizada

```kotlin
@Composable
fun CustomImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Manejar captura
                showPicker = false
            },
            onError = { exception ->
                // Manejar errores
                showPicker = false
            },
            customPermissionHandler = { config ->
                // Manejo personalizado de permisos
                println("Custom permission config: ${config.titleDialogConfig}")
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                // Vista de confirmación personalizada
                CustomConfirmationDialog(
                    result = result,
                    onConfirm = onConfirm,
                    onRetry = onRetry
                )
            },
            preference = CapturePhotoPreference.HIGH_QUALITY
        )
    }
    
    Button(onClick = { showPicker = true }) {
        Text("Take High Quality Photo")
    }
}
```

## Configuración Avanzada

### Diálogos de permisos personalizados

```kotlin
@Composable
fun CustomPermissionDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant Permission")
            }
        }
    )
}

// Uso
ImagePickerLauncher(
    context = LocalContext.current,
    onPhotoCaptured = { /* ... */ },
    onError = { /* ... */ },
    customPermissionHandler = { config ->
        // Mostrar diálogo personalizado
        CustomPermissionDialog(
            title = config.titleDialogConfig,
            description = config.descriptionDialogConfig,
            onConfirm = { /* Lógica para conceder permiso */ }
        )
    }
)
```



### Procesamiento personalizado de fotos

```kotlin
@Composable
fun ImagePickerWithProcessing() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Procesar la foto capturada
            lifecycleScope.launch {
                val processedImage = processImage(result.uri)
                // Usar imagen procesada
            }
        },
        onError = { exception ->
            // Manejar errores de procesamiento
        }
    )
}

suspend fun processImage(uri: Uri): Bitmap {
    return withContext(Dispatchers.IO) {
        // Lógica de procesamiento de imagen
        // Redimensionar, comprimir, aplicar filtros, etc.
    }
}
```

## Configuración específica de plataforma

### Android

```kotlin
// Configuración específica de Android
@Composable
fun AndroidImagePicker() {
    val context = LocalContext.current
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Manejo específico de Android
            if (context is ComponentActivity) {
                // Usar APIs específicas de Android
            }
        },
        onError = { exception ->
            // Manejo de errores específico de Android
        }
    )
}
```

### iOS

```kotlin
// Configuración específica de iOS
@Composable
fun IOSImagePicker() {
    ImagePickerLauncher(
        context = null, // iOS no necesita context
        onPhotoCaptured = { result ->
            // Manejo específico de iOS
        },
        onError = { exception ->
            // Manejo de errores específico de iOS
        }
    )
}
```

## Selección de Galería y Diálogos en iOS

### Soporte multiplataforma para galería

Puedes permitir que los usuarios seleccionen imágenes de la galería tanto en Android como en iOS. En Android, aparece un icono de galería en la UI de la cámara. En iOS, puedes mostrar un diálogo para elegir entre cámara y galería.

### Personalización de textos de diálogo en iOS

Puedes personalizar los textos del diálogo (título, tomar foto, seleccionar de galería, cancelar) en iOS:

```kotlin
ImagePickerLauncher(
    context = ..., // context de la plataforma
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    dialogTitle = "Elige acción", // Solo iOS
    takePhotoText = "Cámara",      // Solo iOS
    selectFromGalleryText = "Galería", // Solo iOS
    cancelText = "Cancelar"         // Solo iOS
)
```

- En Android, estos parámetros se ignoran.
- En iOS, si no se proporcionan, los valores por defecto están en inglés.

## Ejemplos Específicos por Plataforma

### Android Nativo (Jetpack Compose)

Para apps de Android usando Jetpack Compose:

```kotlin
// build.gradle.kts (nivel de app)
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
    implementation("androidx.compose.ui:ui:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
}

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    MaterialTheme {
        ImagePickerScreen()
    }
}

@Composable
fun ImagePickerScreen() {
    var showPicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showPicker = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Tomar Foto")
        }
        
        if (showPicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                onPhotoCaptured = { result ->
                    // Manejar captura de foto
                    println("Foto capturada: ${result.uri}")
                    showPicker = false
                },
                onError = { exception ->
                    // Manejar errores
                    println("Error: ${exception.message}")
                    showPicker = false
                }
            )
        }
    }
}
```

### iOS Nativo (Swift/SwiftUI)

Para apps de iOS usando SwiftUI:

```swift
// Podfile
target 'YourApp' do
  use_frameworks!
  pod 'ImagePickerKMP', :path => '../path/to/your/library'
end

// ContentView.swift
import SwiftUI
import ImagePickerKMP

struct ContentView: View {
    @State private var showImagePicker = false
    @State private var capturedImage: UIImage?
    
    var body: some View {
        VStack {
            if let image = capturedImage {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 200)
            }
            
            Button("Tomar Foto") {
                showImagePicker = true
            }
            .padding()
        }
        .sheet(isPresented: $showImagePicker) {
            ImagePickerView(
                onPhotoCaptured: { result in
                    // Manejar captura de foto
                    print("Foto capturada: \(result.uri)")
                    showImagePicker = false
                },
                onError: { error in
                    // Manejar errores
                    print("Error: \(error.localizedDescription)")
                    showImagePicker = false
                }
            )
        }
    }
}

// ImagePickerView.swift
import SwiftUI
import ImagePickerKMP

struct ImagePickerView: UIViewControllerRepresentable {
    let onPhotoCaptured: (PhotoResult) -> Void
    let onError: (Error) -> Void
    
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = UIViewController()
        
        // Crear ImagePickerKMP launcher
        let imagePicker = ImagePickerLauncher(
            context: nil, // iOS no necesita context
            onPhotoCaptured: onPhotoCaptured,
            onError: onError
        )
        
        // Presentar el image picker
        controller.present(imagePicker, animated: true)
        
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Actualizar si es necesario
    }
}
```

### Kotlin Multiplatform / Compose Multiplatform

Para apps multiplataforma usando KMP/CMP con una sola UI para ambas plataformas:

#### 1. Agregar Dependencias

En tu `build.gradle.kts` (módulo compartido):

```kotlin
kotlin {
    android {
        // Configuración de Android
    }
    
    ios {
        binaries {
            framework {
                baseName = "Shared"
            }
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.ismoy:imagepickerkmp:1.0.0")
            }
        }
    }
}
```

#### 2. Agregar Permisos

**Android**: En tu `androidMain/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

**iOS**: En tu `iosMain/Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>Esta app necesita acceso a la cámara para capturar fotos</string>
```

#### 3. Implementación Compartida

Crea un composable compartido en tu `commonMain`:

```kotlin
// commonMain/kotlin/TuPaquete/SharedImagePicker.kt
@Composable
fun SharedImagePicker(
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    
    Column {
        Button(onClick = { showPicker = true }) {
            Text("Tomar Foto")
        }
        
        if (showPicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                onPhotoCaptured = { result ->
                    onPhotoCaptured(result)
                    showPicker = false
                },
                onError = { exception ->
                    onError(exception)
                    showPicker = false
                }
            )
        }
    }
}
```

#### 4. Uso en Tu App

```kotlin
// commonMain/kotlin/TuPaquete/TuApp.kt
@Composable
fun TuApp() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido a Tu App")
            
            SharedImagePicker(
                onPhotoCaptured = { result ->
                    println("Foto capturada: ${result.uri}")
                    // Manejar la foto capturada
                },
                onError = { exception ->
                    println("Error: ${exception.message}")
                    // Manejar errores
                }
            )
        }
    }
}
```

#### 5. Puntos de Entrada Específicos por Plataforma

**Android**: En tu `androidMain/AndroidActivity.kt`:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TuApp()
        }
    }
}
```

**iOS**: En tu `iosMain/IOSApp.kt`:

```kotlin
@Composable
fun IOSApp() {
    TuApp()
}
```

Este enfoque proporciona una sola UI que funciona en ambas plataformas con el mismo código base.
        onPhotoCaptured = onPhotoCaptured,
        onError = onError
    )
}

// Implementación de Android
@Composable
fun AndroidImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    Column {
        Button(onClick = { showPicker = true }) {
            Text("Tomar Foto")
        }
        
        if (showPicker) {
            SharedImagePicker(
                onPhotoCaptured = { result ->
                    println("Foto capturada en Android: ${result.uri}")
                    showPicker = false
                },
                onError = { exception ->
                    println("Error en Android: ${exception.message}")
                    showPicker = false
                }
            )
        }
    }
}

// Implementación de iOS (en Kotlin)
@Composable
fun IOSImagePicker() {
    var showPicker by remember { mutableStateOf(false) }
    
    Column {
        Button(onClick = { showPicker = true }) {
            Text("Tomar Foto")
        }
        
        if (showPicker) {
            SharedImagePicker(
                onPhotoCaptured = { result ->
                    println("Foto capturada en iOS: ${result.uri}")
                    showPicker = false
                },
                onError = { exception ->
                    println("Error en iOS: ${exception.message}")
                    showPicker = false
                }
            )
        }
    }
}
```

## Solución de Problemas

### Problemas comunes

#### 1. Permiso denegado

**Problema**: Permiso de cámara denegado y no aparece diálogo de reintento.

**Solución**: Asegúrate de usar el componente `RequestCameraPermission`:

```kotlin
@Composable
fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    // La implementación maneja diferencias de plataforma
}
```

#### 2. La cámara no inicia

**Problema**: La cámara no inicia tras conceder el permiso.

**Solución**: Verifica permisos y hardware de cámara:

```kotlin
// Verifica si hay cámara disponible
val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
val cameraIds = cameraManager.cameraIdList
if (cameraIds.isNotEmpty()) {
    // Hay cámara disponible
}
```

#### 3. Errores de build en iOS

**Problema**: El build de iOS falla con errores de linking.

**Solución**: Asegúrate de configurar correctamente el framework:

```kotlin
kotlin {
    ios {
        binaries {
            framework {
                baseName = "ImagePickerKMP"
                isStatic = false
            }
        }
    }
}
```

#### 4. Problemas de memoria

**Problema**: La app se cierra por problemas de memoria con imágenes grandes.

**Solución**: Implementa compresión de imagen:

```kotlin
@Composable
fun ImagePickerWithCompression() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Comprime la imagen antes de procesar
            val compressedImage = compressImage(result.uri)
        }
    )
}
```

### Consejos de depuración

1. **Habilita logs**:
```kotlin
// Añade logs para rastrear problemas
ImagePickerLauncher(
    context = LocalContext.current,
    onPhotoCaptured = { result ->
        Log.d("ImagePicker", "Photo captured: ${result.uri}")
    },
    onError = { exception ->
        Log.e("ImagePicker", "Error: ${exception.message}", exception)
    }
)
```

2. **Verifica permisos**:
```kotlin
// Depura el estado de permisos
fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
```

3. **Prueba en diferentes dispositivos**:
- Prueba en dispositivos físicos, no solo emuladores
- Prueba en diferentes versiones de Android
- Prueba en diferentes versiones de iOS

## Migración desde otras librerías

### Desde CameraX

```kotlin
// Implementación antigua con CameraX
class CameraXImplementation {
    fun startCamera() {
        // Código CameraX
    }
}

// Nueva implementación con ImagePickerKMP
@Composable
fun ImagePickerKMPImplementation() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Manejar captura
        },
        onError = { exception ->
            // Manejar errores
        }
    )
}
```

### Desde UIImagePickerController (iOS)

```swift
// Implementación antigua con UIImagePickerController
let imagePicker = UIImagePickerController()
imagePicker.sourceType = .camera
present(imagePicker, animated: true)

// Nueva implementación con ImagePickerKMP
@Composable
fun ImagePickerKMPImplementation() {
    ImagePickerLauncher(
        context = null,
        onPhotoCaptured = { result ->
            // Manejar captura
        },
        onError = { exception ->
            // Manejar errores
        }
    )
}
```

## Buenas Prácticas

### 1. Manejo de errores

```kotlin
@Composable
fun RobustImagePicker() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            try {
                // Procesar foto de forma segura
                processPhoto(result)
            } catch (e: Exception) {
                // Manejar errores de procesamiento
                showError("Error al procesar la foto: ${e.message}")
            }
        },
        onError = { exception ->
            when (exception) {
                is CameraPermissionException -> {
                    showPermissionError()
                }
                is PhotoCaptureException -> {
                    showCaptureError()
                }
                else -> {
                    showGenericError()
                }
            }
        }
    )
}
```

### 2. Gestión de memoria

```kotlin
@Composable
fun MemoryEfficientImagePicker() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Guarda URI en vez de Bitmap para ahorrar memoria
            imageUri = result.uri
        },
        onError = { exception ->
            // Manejar errores
        }
    )
    
    // Carga la imagen solo cuando sea necesario
    imageUri?.let { uri ->
        AsyncImage(
            model = uri,
            contentDescription = "Captured photo"
        )
    }
}
```

### 3. Experiencia de usuario

```kotlin
@Composable
fun UserFriendlyImagePicker() {
    var isLoading by remember { mutableStateOf(false) }
    
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                isLoading = true
                // Procesar foto
                processPhoto(result) {
                    isLoading = false
                }
            },
            onError = { exception ->
                // Mostrar mensaje de error amigable
                showSnackbar("No se pudo capturar la foto. Intenta de nuevo.")
            }
        )
    }
}
```

## Soporte

Si encuentras problemas durante la integración:

1. **Revisa la documentación**: Consulta esta guía y otros documentos
2. **Busca issues**: Revisa problemas similares en el repositorio de GitHub
3. **Crea un issue**: Proporciona información detallada sobre tu problema
4. **Soporte comunitario**: Pregunta en los foros de la comunidad

Para más detalles, consulta [Guía de Personalización](docs/CUSTOMIZATION_GUIDE.es.md).

Consulta [Referencia de API](docs/API_REFERENCE.es.md) para documentación completa.

Consulta [Ejemplos](docs/EXAMPLES.es.md) para más casos de uso. 