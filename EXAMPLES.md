# Examples

A comprehensive collection of examples showing how to use ImagePickerKMP in various scenarios.

## Table of Contents

- [Basic Examples](#basic-examples)
- [Advanced Examples](#advanced-examples)
- [Custom UI Examples](#custom-ui-examples)
- [Platform-Specific Examples](#platform-specific-examples)
- [Integration Examples](#integration-examples)

## Basic Examples

### 1. Simple Image Picker

The most basic implementation of ImagePickerKMP.

```kotlin
@Composable
fun SimpleImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}
```

### 2. Image Picker with Preview

Show a preview of the captured photo.

```kotlin
@Composable
fun ImagePickerWithPreview() {
    var showImagePicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                capturedImageUri = result.uri
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Column {
        Button(onClick = { showImagePicker = true }) {
            Text("Take Photo")
        }
        
        capturedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
```

### 3. Image Picker with Loading State

Show loading state while processing the photo.

```kotlin
@Composable
fun ImagePickerWithLoading() {
    var showImagePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                isLoading = true
                // Simulate processing
                lifecycleScope.launch {
                    delay(2000) // Simulate processing time
                    capturedImageUri = result.uri
                    isLoading = false
                    showImagePicker = false
                }
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Column {
        Button(onClick = { showImagePicker = true }) {
            Text("Take Photo")
        }
        
        if (isLoading) {
            CircularProgressIndicator()
            Text("Processing photo...")
        }
        
        capturedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
```

## Advanced Examples

### 1. Image Picker with Custom Permissions

Handle permissions with custom dialogs.

```kotlin
@Composable
fun ImagePickerWithCustomPermissions() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            },
            customPermissionHandler = { config ->
                CustomPermissionDialog(
                    title = config.titleDialogConfig,
                    description = config.descriptionDialogConfig,
                    onConfirm = {
                        // Handle permission request
                        requestCameraPermission()
                    },
                    onDismiss = {
                        showImagePicker = false
                    }
                )
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}

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

### 2. Image Picker with Custom Confirmation

Custom confirmation view for captured photos.

```kotlin
@Composable
fun ImagePickerWithCustomConfirmation() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            },
            customConfirmationView = { result, onConfirm, onRetry ->
                CustomConfirmationView(
                    result = result,
                    onConfirm = { onConfirm(result) },
                    onRetry = { onRetry() }
                )
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}

@Composable
fun CustomConfirmationView(
    result: PhotoResult,
    onConfirm: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Photo preview
        AsyncImage(
            model = result.uri,
            contentDescription = "Captured photo",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red
                )
            ) {
                Icon(Icons.Default.Refresh, "Retry")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
            
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Green
                )
            ) {
                Icon(Icons.Default.Check, "Confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Photo")
            }
        }
    }
}
```

### 3. Image Picker with Analytics

Track user interactions with analytics.

```kotlin
@Composable
fun ImagePickerWithAnalytics() {
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = context,
            onPhotoCaptured = { result ->
                // Track successful photo capture
                FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                    putString("photo_uri", result.uri.toString())
                    putLong("photo_size", result.size)
                    putString("photo_format", result.format)
                })
                showImagePicker = false
            },
            onError = { exception ->
                // Track errors
                FirebaseAnalytics.getInstance(context).logEvent("photo_capture_error", Bundle().apply {
                    putString("error_type", exception.javaClass.simpleName)
                    putString("error_message", exception.message)
                })
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { 
        // Track button click
        FirebaseAnalytics.getInstance(context).logEvent("camera_button_clicked", Bundle())
        showImagePicker = true 
    }) {
        Text("Take Photo")
    }
}
```

## Custom UI Examples

### 1. Branded Image Picker

Custom image picker with brand colors and styling.

```kotlin
@Composable
fun BrandedImagePicker() {
    val brandColors = remember {
        mapOf(
            "primary" to Color(0xFF1976D2),
            "secondary" to Color(0xFF42A5F5),
            "accent" to Color(0xFFFF5722)
        )
    }
    
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = brandColors["primary"]!!,
            secondary = brandColors["secondary"]!!
        )
    ) {
        var showImagePicker by remember { mutableStateOf(false) }
        
        if (showImagePicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                onPhotoCaptured = { result ->
                    println("Photo captured: ${result.uri}")
                    showImagePicker = false
                },
                onError = { exception ->
                    println("Error: ${exception.message}")
                    showImagePicker = false
                },
                customPermissionHandler = { config ->
                    BrandedPermissionDialog(
                        config = config,
                        onConfirm = { requestCameraPermission() }
                    )
                }
            )
        }
        
        Button(
            onClick = { showImagePicker = true },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = brandColors["primary"]!!
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Camera, "Camera")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Take Photo", style = MaterialTheme.typography.button)
        }
    }
}

@Composable
fun BrandedPermissionDialog(
    config: PermissionConfig,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colors.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = config.titleDialogConfig,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = config.descriptionDialogConfig,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Text("Grant Permission")
            }
        }
    }
}
```

### 2. Minimal Image Picker

Clean, minimal design for the image picker.

```kotlin
@Composable
fun MinimalImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo captured: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    FloatingActionButton(
        onClick = { showImagePicker = true },
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(Icons.Default.Camera, "Take Photo")
    }
}
```

### 3. Card-Based Image Picker

Image picker with card-based design.

```kotlin
@Composable
fun CardBasedImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                capturedImageUri = result.uri
                showImagePicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Photo Capture",
                style = MaterialTheme.typography.h6
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (capturedImageUri != null) {
                AsyncImage(
                    model = capturedImageUri,
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Button(
                onClick = { showImagePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(Icons.Default.Camera, "Camera")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Photo")
            }
        }
    }
}
```

## Platform-Specific Examples

### 1. Android-Specific Features

```kotlin
@Composable
fun AndroidSpecificImagePicker() {
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = context,
            onPhotoCaptured = { result ->
                // Android-specific handling
                if (context is ComponentActivity) {
                    // Use Android-specific APIs
                    shareImage(context, result.uri)
                }
                showImagePicker = false
            },
            onError = { exception ->
                // Android-specific error handling
                if (context is ComponentActivity) {
                    showAndroidToast(context, exception.message)
                }
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo (Android)")
    }
}

fun shareImage(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Image"))
}

fun showAndroidToast(context: Context, message: String?) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
```

### 2. iOS-Specific Features

```kotlin
@Composable
fun IOSSpecificImagePicker() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = null, // iOS doesn't need context
            onPhotoCaptured = { result ->
                // iOS-specific handling
                println("Photo captured on iOS: ${result.uri}")
                showImagePicker = false
            },
            onError = { exception ->
                // iOS-specific error handling
                println("iOS error: ${exception.message}")
                showImagePicker = false
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo (iOS)")
    }
}
```

### 4. Gallery Selection and iOS Dialog Customization

```kotlin
@Composable
fun GalleryAndCustomDialogExample() {
    var showPicker by remember { mutableStateOf(false) }
    if (showPicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                println("Photo: ${result.uri}")
                showPicker = false
            },
            onError = { exception ->
                println("Error: ${exception.message}")
                showPicker = false
            },
            dialogTitle = "Choose action", // iOS only
            takePhotoText = "Camera",      // iOS only
            selectFromGalleryText = "Gallery", // iOS only
            cancelText = "Dismiss"         // iOS only
        )
    }
    Button(onClick = { showPicker = true }) {
        Text("Pick or Take Photo")
    }
}
```

## Integration Examples

### 1. Integration with ViewModel

```kotlin
class ImagePickerViewModel : ViewModel() {
    private val _photoResult = MutableStateFlow<PhotoResult?>(null)
    val photoResult: StateFlow<PhotoResult?> = _photoResult.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun onPhotoCaptured(result: PhotoResult) {
        _photoResult.value = result
        _isLoading.value = true
        
        viewModelScope.launch {
            try {
                // Process photo
                processPhoto(result)
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun onError(exception: Exception) {
        _error.value = exception.message
    }
    
    fun clearError() {
        _error.value = null
    }
    
    private suspend fun processPhoto(result: PhotoResult) {
        // Simulate photo processing
        delay(2000)
    }
}

@Composable
fun ImagePickerWithViewModel() {
    val viewModel: ImagePickerViewModel = viewModel()
    val photoResult by viewModel.photoResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                viewModel.onPhotoCaptured(result)
                showImagePicker = false
            },
            onError = { exception ->
                viewModel.onError(exception)
                showImagePicker = false
            }
        )
    }
    
    Column {
        Button(onClick = { showImagePicker = true }) {
            Text("Take Photo")
        }
        
        if (isLoading) {
            CircularProgressIndicator()
            Text("Processing photo...")
        }
        
        error?.let { errorMessage ->
            Text(
                text = "Error: $errorMessage",
                color = Color.Red
            )
            Button(onClick = { viewModel.clearError() }) {
                Text("Dismiss")
            }
        }
        
        photoResult?.let { result ->
            AsyncImage(
                model = result.uri,
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
```

### 2. Integration with Navigation

```kotlin
@Composable
fun ImagePickerWithNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("camera") {
            CameraScreen(navController)
        }
        composable("preview/{uri}") { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            PreviewScreen(uri, navController)
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Image Picker")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("camera") }) {
            Text("Take Photo")
        }
    }
}

@Composable
fun CameraScreen(navController: NavController) {
    var showImagePicker by remember { mutableStateOf(true) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                showImagePicker = false
                navController.navigate("preview/${result.uri}")
            },
            onError = { exception ->
                showImagePicker = false
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun PreviewScreen(uri: String?, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        uri?.let { imageUri ->
            AsyncImage(
                model = imageUri,
                contentDescription = "Captured photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
            Button(onClick = { navController.navigate("home") }) {
                Text("Home")
            }
        }
    }
}
```

### 3. Integration with State Management

```kotlin
data class ImagePickerState(
    val isCapturing: Boolean = false,
    val capturedPhoto: PhotoResult? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

sealed class ImagePickerEvent {
    object StartCapture : ImagePickerEvent()
    data class PhotoCaptured(val result: PhotoResult) : ImagePickerEvent()
    data class ErrorOccurred(val exception: Exception) : ImagePickerEvent()
    object ClearError : ImagePickerEvent()
    object ProcessPhoto : ImagePickerEvent()
}

@Composable
fun ImagePickerWithStateManagement() {
    var state by remember { mutableStateOf(ImagePickerState()) }
    
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                state = state.copy(
                    isCapturing = false,
                    capturedPhoto = result,
                    error = null
                )
                showImagePicker = false
            },
            onError = { exception ->
                state = state.copy(
                    isCapturing = false,
                    error = exception.message
                )
                showImagePicker = false
            }
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isCapturing -> {
                Text("Capturing photo...")
                CircularProgressIndicator()
            }
            state.isLoading -> {
                Text("Processing photo...")
                CircularProgressIndicator()
            }
            state.error != null -> {
                Text(
                    text = "Error: ${state.error}",
                    color = Color.Red
                )
                Button(onClick = {
                    state = state.copy(error = null)
                }) {
                    Text("Dismiss")
                }
            }
            state.capturedPhoto != null -> {
                AsyncImage(
                    model = state.capturedPhoto!!.uri,
                    contentDescription = "Captured photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(onClick = {
                    state = state.copy(isLoading = true)
                    // Process photo
                    lifecycleScope.launch {
                        delay(2000) // Simulate processing
                        state = state.copy(isLoading = false)
                    }
                }) {
                    Text("Process Photo")
                }
            }
            else -> {
                Button(onClick = {
                    state = state.copy(isCapturing = true)
                    showImagePicker = true
                }) {
                    Text("Take Photo")
                }
            }
        }
    }
}
```

## Support

For examples-related issues:

1. **Test the examples**: Try running the provided examples
2. **Check compatibility**: Ensure you're using compatible versions
3. **Adapt to your needs**: Modify examples to fit your requirements
4. **Contact support**: Reach out for example-specific help

For more information, refer to:
- [API Reference](API_REFERENCE.md)
- [Integration Guide](INTEGRATION_GUIDE.md)
- [Customization Guide](CUSTOMIZATION_GUIDE.md)

## Configuración Básica con Crashlytics

### 1. Configurar la Aplicación

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Establecer el contexto para que la librería pueda inicializar Firebase
        CrashlyticsManager.setApplicationContext(this)
        
        // Inicializar Crashlytics con la configuración recomendada
        CrashlyticsManager.initialize(CrashlyticsConfig.RECOMMENDED)
    }
}
```

### 2. Usar ImagePickerLauncher

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Column {
                Button(
                    onClick = {
                        ImagePickerLauncher.launchImagePicker(
                            context = this@MainActivity,
                            onImageSelected = { uri ->
                                // Manejar imagen seleccionada
                                println("Imagen seleccionada: $uri")
                            },
                            onError = { exception ->
                                // Los errores se registrarán automáticamente en Crashlytics
                                println("Error: ${exception.message}")
                            }
                        )
                    }
                ) {
                    Text("Seleccionar Imagen")
                }
            }
        }
    }
}
```

## Configuración Avanzada

### Configuración Personalizada de Crashlytics

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Establecer el contexto
        CrashlyticsManager.setApplicationContext(this)
        
        // Configuración personalizada
        CrashlyticsManager.initialize(
            CrashlyticsConfig(
                enabled = true,
                logExceptions = true,
                logMessages = true,
                trackUserSessions = true,
                customKeys = mapOf(
                    "app_version" to BuildConfig.VERSION_NAME,
                    "build_type" to BuildConfig.BUILD_TYPE,
                    "device_model" to Build.MODEL
                )
            )
        )
    }
}
```

### Usar CameraPreview con Crashlytics

```kotlin
@Composable
fun CameraScreen() {
    CameraPreview(
        onPhotoCaptured = { uri ->
            // Manejar foto capturada
            println("Foto capturada: $uri")
        },
        onError = { exception ->
            // Los errores se registrarán automáticamente
            println("Error de cámara: ${exception.message}")
        },
        crashlyticsConfig = CrashlyticsConfig.RECOMMENDED
    )
}
```

## Logs Esperados

Cuando configures correctamente Crashlytics, verás logs como estos:

```
🔥 Firebase Crashlytics: Initializing with config enabled=true
🔥 Firebase Crashlytics Android: Application context set
🔥 Firebase Crashlytics: Session tracking enabled
🔥 Firebase Crashlytics: Set custom key session_start = started
🔥 Firebase Crashlytics: Sending test crash to verify connection...
🔥 Firebase Crashlytics Android: Recording exception - ImagePickerKMP Test Crash - Library initialized successfully
🔥 Firebase Crashlytics Android: Exception recorded successfully!
🔥 Firebase Crashlytics: Test crash sent successfully!
```

## Solución de Problemas

### Si ves "Firebase not initialized"

1. **Asegúrate de establecer el contexto**:
```kotlin
CrashlyticsManager.setApplicationContext(this)
```

2. **Verifica que tu google-services.json esté en la carpeta correcta**:
```
app/google-services.json
```

3. **Asegúrate de que las dependencias estén añadidas**:
```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.2")
    implementation("com.google.firebase:firebase-analytics-ktx:21.5.1")
}
```

### Si no aparecen logs

1. **Verifica que Crashlytics esté habilitado**:
```kotlin
CrashlyticsConfig.RECOMMENDED // en lugar de DISABLED
```

2. **Asegúrate de que la inicialización se ejecute en onCreate()**:
```kotlin
override fun onCreate() {
    super.onCreate()
    CrashlyticsManager.initialize(CrashlyticsConfig.RECOMMENDED)
}
```

## Configuración Completa

### build.gradle.kts (App)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")  // IMPORTANTE
    id("com.google.firebase.crashlytics")  // IMPORTANTE
}

dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.0")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.2")
    implementation("com.google.firebase:firebase-analytics-ktx:21.5.1")
}
```

**IMPORTANTE**: Los plugins de Firebase deben estar en la aplicación, NO en la librería.

### AndroidManifest.xml

```xml
<application
    android:name=".MyApplication"
    ...>
    
    <activity
        android:name=".MainActivity"
        ...>
    </activity>
</application>
```

### MyApplication.kt

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configurar Crashlytics
        CrashlyticsManager.setApplicationContext(this)
        CrashlyticsManager.initialize(CrashlyticsConfig.RECOMMENDED)
    }
}
```

### MainActivity.kt

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ImagePickerApp()
        }
    }
}

@Composable
fun ImagePickerApp() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showImagePicker = true }
        ) {
            Text("Seleccionar Imagen")
        }
        
        if (showImagePicker) {
            ImagePickerLauncher(
                context = LocalContext.current,
                onImageSelected = { uri ->
                    showImagePicker = false
                    println("Imagen seleccionada: $uri")
                },
                onError = { exception ->
                    showImagePicker = false
                    println("Error: ${exception.message}")
                }
            )
        }
    }
}
```

## Verificar que Funciona

1. **Ejecuta la aplicación**
2. **Busca los logs en Logcat**:
   ```
   🔥 Firebase Crashlytics: Initializing with config enabled=true
   ```
3. **Verifica en Firebase Console** que aparezcan los datos
4. **Prueba el crash de prueba** que se envía automáticamente

## Beneficios

- ✅ **Inicialización automática**: La librería maneja todo
- ✅ **Logs detallados**: Puedes ver qué se está enviando
- ✅ **Crash de prueba**: Verificación automática
- ✅ **Manejo de errores**: Sistema robusto
- ✅ **Configuración flexible**: Los desarrolladores pueden personalizar 

# Ejemplos de Integración - ImagePickerKMP

## Selección simple y múltiple de imágenes con ImagePickerLauncher

A partir de la versión actual, puedes usar `ImagePickerLauncher` para permitir a tus usuarios tomar una foto con la cámara o seleccionar una o varias imágenes desde la galería, de forma multiplataforma.

### Ejemplo completo en Compose

```kotlin
@Composable
fun CameraScreen(context: Any?) {
    var showImagePicker by remember { mutableStateOf(false) }
    var capturedImages by remember { mutableStateOf<List<CameraPhotoHandler.PhotoResult>>(emptyList()) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (showImagePicker) {
                    ImagePickerLauncher(
                        context = context,
                        onPhotoCaptured = { photoResult ->
                            // Cuando se toma una foto con la cámara
                            capturedImages = listOf(photoResult)
                            showImagePicker = false
                        },
                        onPhotosSelected = { photoResults ->
                            // Cuando se seleccionan una o varias imágenes de la galería
                            capturedImages = photoResults
                            showImagePicker = false
                        },
                        onError = { exception ->
                            showImagePicker = false
                        },
                        preference = CapturePhotoPreference.QUALITY,
                        dialogTitle = "Seleccionar imagen",
                        takePhotoText = "Tomar foto",
                        selectFromGalleryText = "Seleccionar de galería",
                        cancelText = "Cancelar",
                        allowMultiple = true // Cambia a false para solo una imagen
                    )
                } else if (capturedImages.isNotEmpty()) {
                    LazyRow {
                        items(capturedImages) { image ->
                            AsyncImage(
                                model = image.uri,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedButton(
                    onClick = {
                        showImagePicker = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Abrir selector de imágenes")
                }
            }
        }
    }
}
```

### Puntos clave
- Usa `onPhotoCaptured` para manejar la foto tomada con la cámara (siempre una sola imagen).
- Usa `onPhotosSelected` para manejar la selección desde galería (puede ser una o varias imágenes, según `allowMultiple`).
- Muestra las imágenes usando una lista (`LazyRow`, `Column`, etc.).
- El callback siempre recibe una lista, aunque solo se seleccione una imagen.

---

¿Dudas? Consulta la guía de arquitectura o abre un issue. 