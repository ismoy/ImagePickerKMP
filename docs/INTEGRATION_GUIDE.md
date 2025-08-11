This document is also available in Spanish: [INTEGRATION_GUIDE.es.md](INTEGRATION_GUIDE.es.md)

# Integration Guide

This guide will help you integrate ImagePickerKMP into your Kotlin Multiplatform project for both Android and iOS platforms.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Android Setup](#android-setup)
- [iOS Setup](#ios-setup)
- [Basic Integration](#basic-integration)
- [Advanced Configuration](#advanced-configuration)
- [Troubleshooting](#troubleshooting)
- [Migration from Other Libraries](#migration-from-other-libraries)
- [Gallery Selection & iOS Dialog Customization](#gallery-selection-ios-dialog-customization)
- [Platform-Specific Examples](#platform-specific-examples)

## Prerequisites

Before integrating ImagePickerKMP, ensure you have:

- **Kotlin Multiplatform project** set up
- **Android Studio** or **IntelliJ IDEA** with Kotlin plugin
- **Xcode** (for iOS development)
- **Minimum SDK versions**:
  - Android: API 21+
  - iOS: iOS 12.0+

## Android Setup

### 1. Add Dependencies

In your `build.gradle.kts` (app level):

```kotlin
dependencies {
    implementation("io.github.ismoy:imagepickerkmp:1.0.2")
}
```

### 2. Add Permissions

In your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

### 3. Configure ProGuard (if using)

Add to your `proguard-rules.pro`:

```proguard
-keep class io.github.ismoy.imagepickerkmp.** { *; }
```

## iOS Setup

### 1. Add Dependencies

In your `build.gradle.kts` (shared module):

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

### 2. Add Camera Permission

In your `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos</string>
```

### 3. Configure Podfile (if using CocoaPods)

```ruby
target 'YourApp' do
  use_frameworks!
  pod 'ImagePickerKMP', :path => '../path/to/your/library'
end
```

## Basic Integration

### Simple Implementation

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

### With Custom Configuration

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

## Advanced Configuration

### Custom Permission Dialogs

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
// The views
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
## Platform-Specific Configuration

### Android

```kotlin
// Android-specific configuration
@Composable
fun AndroidImagePicker() {
    ImagePickerLauncher(
        onPhotoCaptured = { result ->
            // Android-specific handling
        },
        onError = { exception ->
            // Android-specific error handling
        },
        onDismiss = {
            // Android-specific cancellation handling
        }
    )
}
```

### iOS

```kotlin
// iOS-specific configuration
@Composable
fun IOSImagePicker() {
    ImagePickerLauncher(
        onPhotoCaptured = { result ->
            // iOS-specific handling
        },
        onError = { exception ->
            // iOS-specific error handling
        },
        onDismiss = {
            // iOS-specific cancellation handling
        }
    )
}
```

## Gallery Selection & iOS Dialog Customization

### Multiplatform Gallery Support

You can allow users to select images from the gallery on both Android and iOS. On Android, a gallery icon appears in the camera UI. On iOS, you can show a dialog to choose between camera and gallery.

### iOS Dialog Text Customization

You can customize the dialog texts (title, take photo, select from gallery, cancel) on iOS:

```kotlin
ImagePickerLauncher(
    onPhotoCaptured = { result -> /* ... */ },
    onError = { exception -> /* ... */ },
    dialogTitle = "Choose action", // iOS only
    takePhotoText = "Camera",      // iOS only
    selectFromGalleryText = "Gallery", // iOS only
    cancelText = "Cancel"         // iOS only
)
```

- On Android, these parameters are ignored.
- On iOS, if not provided, defaults are in English.

## Gallery Confirmation View Customization

### cameraCaptureConfig Support in GalleryPickerLauncher

Now `GalleryPickerLauncher` supports the optional `cameraCaptureConfig` parameter, which allows you to use the same custom confirmation view as `ImagePickerLauncher`. This is especially useful when you want to maintain a consistent user experience between camera capture and gallery selection.

### Basic Example with Custom Confirmation View

```kotlin
@Composable
fun GalleryPickerWithCustomConfirmation() {
    var showGalleryPicker by remember { mutableStateOf(false) }
    
    if (showGalleryPicker) {
        GalleryPickerLauncher(
            onPhotosSelected = { results ->
                // Handle selected images
                showGalleryPicker = false
            },
            onError = { exception ->
                // Handle errors
                showGalleryPicker = false
            },
            onDismiss = {
                // Handle cancellation
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
        Text("Select from Gallery")
    }
}
```

### Advanced Example with Complete Configuration

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
                println("Error selecting image: ${exception.message}")
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
                                // In gallery, retry means select again
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
            Text("Select Multiple Images")
        }
        
        if (selectedImages.isNotEmpty()) {
            Text("Selected images: ${selectedImages.size}")
            LazyRow {
                items(selectedImages) { image ->
                    AsyncImage(
                        model = image.uri,
                        contentDescription = "Selected image",
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
            text = "Confirm selection",
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
                contentDescription = "Selected image",
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
                Text("Select another")
            }
            
            Button(
                onClick = { onConfirm(result) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirm")
            }
        }
    }
}
```

### Benefits of using cameraCaptureConfig in GalleryPickerLauncher

- **UI Consistency**: Maintain the same confirmation experience between camera and gallery
- **Code Reusability**: Use the same custom confirmation component
- **Granular Control**: Fully customize the confirmation experience
- **Flexibility**: Allows different confirmation flows based on your needs

## Platform-Specific Examples

### Android Native (Jetpack Compose)

For Android apps using Jetpack Compose:

```kotlin
// build.gradle.kts (app level)
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
## Selecting Images from Gallery
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
        allowMultiple = false, // Change to true if you need to select multiple images
        mimeTypes = mutableListOf("image/jpeg", "image/png"), // Optional: Filter by file types
        cameraCaptureConfig = CameraCaptureConfig( // Optional: For custom confirmation view
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

For multiplatform apps using KMP/CMP with a single UI for both platforms:

#### 1. Add Dependencies

In your `build.gradle.kts` (shared module):

```kotlin
kotlin {
    android {
        // Android configuration
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

#### 2. Add Permissions

**Android**: In your `androidMain/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="true" />
```

**iOS**: In your `iosMain/Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>This app needs camera access to capture photos</string>
```

#### 3. Shared Implementation

In your `commonMain/kotlin/YourPackage/App.kt`:
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
## Selecting Images from Gallery
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
        allowMultiple = false, // Change to true if you need to select multiple images
        mimeTypes = mutableListOf("image/jpeg", "image/png"), // Optional: Filter by file types
        cameraCaptureConfig = CameraCaptureConfig( // Optional: For custom confirmation view
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