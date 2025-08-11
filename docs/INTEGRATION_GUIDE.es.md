This document is also available in English: [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)

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
    implementation("io.github.ismoy:imagepickerkmp:1.0.2")
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
        config = ImagePickerConfig(
          onPhotoCaptured = { result ->
            cameraPhoto = result
            showPicker = false
            isPickerSheetVisible = false
          },
          onError = {
            showCameraPicker = false
            isPickerSheetVisible = false
          },
          onDismiss = {
            showCameraPicker = false
            isPickerSheetVisible = false
          }
        )
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
        config = ImagePickerConfig(
          onPhotoCaptured = { result ->
            cameraPhoto = result
            showPicker = false
            isPickerSheetVisible = false
          },
          onError = {
            showPicker = false
            isPickerSheetVisible = false
          },
          onDismiss = {
            showPicker = false
            isPickerSheetVisible = false
          },
          customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
            isPickerSheetVisible = true
            MyCustomBottomSheet(
              onTakePhoto = onTakePhoto,
              onSelectFromGallery = onSelectFromGallery,
              onDismiss = {
                isPickerSheetVisible = false
                onCancel()
                showCameraPicker = false
              }
            )
          },
          cameraCaptureConfig = CameraCaptureConfig(
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
              customConfirmationView = { photoResult, onConfirm, onRetry ->
                YourCustomConfirmationView(
                  result = photoResult,
                  onConfirm = onConfirm,
                  onRetry = onRetry
                )
              }
            )
          )
        )
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
var showCameraPicker by remember { mutableStateOf(false) }
var isPickerSheetVisible by remember { mutableStateOf(false) }
var cameraPhoto by remember { mutableStateOf<CameraPhotoHandler.PhotoResult?>(null) }

if(showCameraPicker){
  ImagePickerLauncher(
    config = ImagePickerConfig(
      onPhotoCaptured = { result ->
        cameraPhoto = result
        showCameraPicker = false
        isPickerSheetVisible = false
      },
      onError = {
        showCameraPicker = false
        isPickerSheetVisible = false
      },
      onDismiss = {
        showCameraPicker = false
        isPickerSheetVisible = false
      },
      customPickerDialog = { onTakePhoto, onSelectFromGallery, onCancel ->
        isPickerSheetVisible = true
        MyCustomBottomSheet(
          onTakePhoto = onTakePhoto,
          onSelectFromGallery = onSelectFromGallery,
          onDismiss = {
            isPickerSheetVisible = false
            onCancel()
            showCameraPicker = false
          }
        )
      },
      cameraCaptureConfig = CameraCaptureConfig(
        permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
          customConfirmationView = { photoResult, onConfirm, onRetry ->
            YourCustomConfirmationView(
              result = photoResult,
              onConfirm = onConfirm,
              onRetry = onRetry
            )
          }
        )
      )
    )
  )
}
// La vistas
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyCustomBottomSheet(
  onTakePhoto: () -> Unit,
  onSelectFromGallery: () -> Unit,
  onDismiss: () -> Unit
) {
  val bottomSheetState = rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Expanded,
    skipHalfExpanded = true
  )
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(bottomSheetState.currentValue) {
    if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
      onDismiss()
    }
  }

  ModalBottomSheetLayout(
    sheetState = bottomSheetState,
    sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    sheetElevation = 16.dp,
    sheetBackgroundColor = MaterialTheme.colors.surface,
    scrimColor = Color.Black.copy(alpha = 0.35f),
    sheetContent = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp, start = 20.dp, end = 20.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .width(44.dp)
            .height(5.dp)
            .padding(bottom = 20.dp)
            .align(Alignment.CenterHorizontally)
            .then(
              Modifier
                .padding(top = 2.dp)
            )
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .align(Alignment.Center)
              .padding(horizontal = 12.dp)
              .background(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50)
              )
          )
        }

        Text(
          text = "Select image source",
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colors.onSurface.copy(alpha = 0.87f),
          modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
          text = "Choose an option to continue",
          fontSize = 13.sp,
          color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
          modifier = Modifier.padding(bottom = 20.dp)
        )

        SheetAction(
          emoji = "📷",
          title = "Take a photo",
          subtitle = "Open the camera",
          tint = MaterialTheme.colors.primary,
          onClick = {
            coroutineScope.launch {
              bottomSheetState.hide()
              onTakePhoto()
            }
          }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SheetAction(
          emoji = "🖼️",
          title = "Select from gallery",
          subtitle = "Explore images from your device",
          tint = MaterialTheme.colors.primary,
          onClick = {
            coroutineScope.launch {
              bottomSheetState.hide()
              onSelectFromGallery()
            }
          }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
          onClick = {
            coroutineScope.launch {
              bottomSheetState.hide()
              onDismiss()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Text(
            text = "Cancel",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            fontSize = 15.sp
          )
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) {
    Box(modifier = Modifier.fillMaxSize())
  }
}

@Composable
private fun SheetAction(
  emoji: String,
  title: String,
  subtitle: String?,
  tint: Color,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(14.dp)
  androidx.compose.material.Surface(
    shape = shape,
    color = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
    contentColor = MaterialTheme.colors.onSurface,
    elevation = 0.dp,
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .clip(shape)
      .padding(0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clickable { onClick() },
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start
    ) {
      Box(
        modifier = Modifier
          .width(40.dp)
          .height(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = emoji, fontSize = 20.sp)
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colors.onSurface
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            fontSize = 13.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
          )
        }
      }
    }
  }
}

@Composable
fun YourCustomConfirmationView(
  result: CameraPhotoHandler.PhotoResult,
  onConfirm: (CameraPhotoHandler.PhotoResult) -> Unit,
  onRetry: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Review photo",
      fontSize = 18.sp,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
      modifier = Modifier.padding(bottom = 12.dp)
    )

    Card(
      shape = RoundedCornerShape(20.dp),
      elevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      AsyncImage(
        model = result.uri,
        contentDescription = "Captured photo preview",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = { onRetry() },
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
      ) {
        Text(text = "Retry")
      }

      Button(
        onClick = { onConfirm(result) },
        modifier = Modifier
          .weight(1f)
          .height(52.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(text = "Confirm", color = Color.White)
      }
    }
  }
}
```
## Configuración específica de plataforma

### Android

```kotlin
// Configuración específica de Android
@Composable
fun AndroidImagePicker() {
    ImagePickerLauncher(
        onPhotoCaptured = { result ->
            // Manejo específico de Android
        },
        onError = { exception ->
            // Manejo de errores específico de Android
        },
        onDismiss = {
            // Manejo de cancelación específica de Android
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
        onPhotoCaptured = { result ->
            // Manejo específico de iOS
        },
        onError = { exception ->
            // Manejo de errores específico de iOS
        },
        onDismiss = {
            // Manejo de cancelación específica de iOS
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

## Personalización de Vista de Confirmación en Galería

### Soporte para cameraCaptureConfig en GalleryPickerLauncher

Ahora `GalleryPickerLauncher` soporta el parámetro opcional `cameraCaptureConfig`, que permite usar la misma vista de confirmación personalizada que `ImagePickerLauncher`. Esto es especialmente útil cuando quieres mantener una experiencia de usuario consistente entre la captura de cámara y la selección de galería.

### Ejemplo básico con vista de confirmación personalizada

```kotlin
@Composable
fun GalleryPickerWithCustomConfirmation() {
    var showGalleryPicker by remember { mutableStateOf(false) }
    
    if (showGalleryPicker) {
        GalleryPickerLauncher(
            onPhotosSelected = { results ->
                // Manejar imágenes seleccionadas
                showGalleryPicker = false
            },
            onError = { exception ->
                // Manejar errores
                showGalleryPicker = false
            },
            onDismiss = {
                // Manejar cancelación
                showGalleryPicker = false
            },
            allowMultiple = false,
            mimeTypes = listOf("image/jpeg", "image/png"),
            cameraCaptureConfig = CameraCaptureConfig(
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        YourCustomConfirmationView(
                            result = photoResult,
                            onConfirm = onConfirm,
                            onRetry = onRetry
                        )
                    }
                )
            )
        )
    }
    
    Button(onClick = { showGalleryPicker = true }) {
        Text("Seleccionar de Galería")
    }
}
```

### Ejemplo avanzado con configuración completa

```kotlin
@Composable
fun AdvancedGalleryPickerWithConfirmation() {
    var showGalleryPicker by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }
    
    if (showGalleryPicker) {
        GalleryPickerLauncher(
            onPhotosSelected = { results ->
                selectedImages = results
                showGalleryPicker = false
            },
            onError = { exception ->
                println("Error al seleccionar imagen: ${exception.message}")
                showGalleryPicker = false
            },
            onDismiss = {
                showGalleryPicker = false
            },
            allowMultiple = true,
            mimeTypes = listOf("image/jpeg", "image/png", "image/webp"),
            selectionLimit = 5,
            cameraCaptureConfig = CameraCaptureConfig(
                preference = CapturePhotoPreference.QUALITY,
                permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                    customConfirmationView = { photoResult, onConfirm, onRetry ->
                        CustomGalleryConfirmationView(
                            result = photoResult,
                            onConfirm = { confirmedResult ->
                                onConfirm(confirmedResult)
                            },
                            onRetry = {
                                // En galería, retry significa volver a seleccionar
                                onRetry()
                            }
                        )
                    }
                )
            )
        )
    }
    
    Column {
        Button(onClick = { showGalleryPicker = true }) {
            Text("Seleccionar Múltiples Imágenes")
        }
        
        if (selectedImages.isNotEmpty()) {
            Text("Imágenes seleccionadas: ${selectedImages.size}")
            LazyRow {
                items(selectedImages) { image ->
                    AsyncImage(
                        model = image.uri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomGalleryConfirmationView(
    result: CameraPhotoHandler.PhotoResult,
    onConfirm: (CameraPhotoHandler.PhotoResult) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Confirmar selección",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AsyncImage(
                model = result.uri,
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onRetry() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Seleccionar otra")
            }
            
            Button(
                onClick = { onConfirm(result) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirmar")
            }
        }
    }
}
```

### Ventajas de usar cameraCaptureConfig en GalleryPickerLauncher

- **Consistencia de UI**: Mantén la misma experiencia de confirmación entre cámara y galería
- **Reutilización de código**: Usa el mismo componente de confirmación personalizado
- **Control granular**: Personaliza completamente la experiencia de confirmación
- **Flexibilidad**: Permite diferentes flujos de confirmación según tus necesidades

## Ejemplos Específicos por Plataforma

### Android Nativo (Jetpack Compose)

Para apps de Android usando Jetpack Compose:

```kotlin
// build.gradle.kts (nivel de app)
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.2")
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
    var showCameraPicker by remember { mutableStateOf(false) }

  ImagePickerLauncher(
    config = ImagePickerConfig(
      onPhotoCaptured = { result ->
        showCameraPicker = false
      },
      onError = {
        showCameraPicker = false
        isPickerSheetVisible = false
      },
      onDismiss = {
        showCameraPicker = false
      }
    )
  )
}
```
## Seleccionando Imágenes de la Galería
```kotlin
@Composable
fun ImagePickerScreen() {
    var showGalleryPicker by remember { mutableStateOf(false) }
    
    GalleryPickerLauncher(
        onPhotosSelected = { results ->
            showGalleryPicker = false
        },
        onError = {
            showGalleryPicker = false
        },
        onDismiss = {
            showGalleryPicker = false
        },
        allowMultiple = false, // Cambia a true si necesitas seleccionar múltiples imágenes
        mimeTypes = mutableListOf("image/jpeg", "image/png"), // Opcional: Filtra por tipos de archivo
        cameraCaptureConfig = CameraCaptureConfig( // Opcional: Para vista de confirmación personalizada
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    YourCustomConfirmationView(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
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
                implementation("io.github.ismoy:imagepickerkmp:@lastVersion")
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

en tu `commonMain/kotlin/TuPaquete/App.kt`:
```kotlin
@Composable
    fun ImagePickerScreen() {
    var showCameraPicker by remember { mutableStateOf(false) }
    
    ImagePickerLauncher(
    config = ImagePickerConfig(
    onPhotoCaptured = { result ->
    showCameraPicker = false
    },
    onError = {
    showCameraPicker = false
    isPickerSheetVisible = false
    },
    onDismiss = {
    showCameraPicker = false
    }
    )
    )
    }
```
## Seleccionando Imágenes de la Galería
```kotlin
@Composable
fun ImagePickerScreen() {
    var showGalleryPicker by remember { mutableStateOf(false) }
    
    GalleryPickerLauncher(
        onPhotosSelected = { results ->
            showGalleryPicker = false
        },
        onError = {
            showGalleryPicker = false
        },
        onDismiss = {
            showGalleryPicker = false
        },
        allowMultiple = false, // Cambia a true si necesitas seleccionar múltiples imágenes
        mimeTypes = mutableListOf("image/jpeg", "image/png"), // Opcional: Filtra por tipos de archivo
        cameraCaptureConfig = CameraCaptureConfig( // Opcional: Para vista de confirmación personalizada
            permissionAndConfirmationConfig = PermissionAndConfirmationConfig(
                customConfirmationView = { photoResult, onConfirm, onRetry ->
                    YourCustomConfirmationView(
                        result = photoResult,
                        onConfirm = onConfirm,
                        onRetry = onRetry
                    )
                }
            )
        )
    )
}
```