# Camera Permission Handling in Android and iOS

## Overview

This document explains how camera permissions are handled in the ImagePickerKMP library for both Android and iOS platforms, including the differences in behavior, implementation details, and examples.

## Platform Differences

### Android Permission System

Android uses a permission system where:
- Permissions are requested at runtime (API 23+)
- Users can grant/deny permissions multiple times
- Apps can request permissions again after denial
- Users can manually enable/disable permissions in Settings

### iOS Permission System

iOS uses a more restrictive permission system where:
- Permissions are requested once per app installation
- After first denial, users must go to Settings to enable permissions
- No automatic retry mechanism exists
- Parental controls can restrict permissions

## Implementation Details

### Android Implementation

#### Permission States in Android

```kotlin
// Android permission states
PackageManager.PERMISSION_GRANTED    // Permission granted
PackageManager.PERMISSION_DENIED     // Permission denied
```

#### Android Permission Flow

```kotlin
@Composable
actual fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    var permissionDeniedCount by remember { mutableIntStateOf(0) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onResult(true)
        } else {
            permissionDeniedCount++
            when {
                permissionDeniedCount >= 2 -> {
                    // Mark as permanently denied after 2 attempts
                    onPermissionPermanentlyDenied()
                }
                else -> {
                    // Show retry dialog
                    showRationale = true
                }
            }
        }
    }
}
```

#### Android Permission Request Example

```kotlin
// Example: Requesting camera permission in Android
val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        // Permission granted - proceed with camera
        startCamera()
    } else {
        // Permission denied - show retry dialog
        showPermissionDialog()
    }
}

// Launch permission request
permissionLauncher.launch(Manifest.permission.CAMERA)
```

### iOS Implementation

#### Permission States in iOS

```kotlin
// iOS permission states
AVAuthorizationStatusAuthorized      // Permission granted
AVAuthorizationStatusNotDetermined  // First time request
AVAuthorizationStatusDenied         // Permission denied
AVAuthorizationStatusRestricted     // Restricted by parental controls
```

#### iOS Permission Flow

```kotlin
@Composable
actual fun RequestCameraPermission(
    onPermissionPermanentlyDenied: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        val currentStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (currentStatus) {
            AVAuthorizationStatusAuthorized -> {
                onResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                requestCameraAccess { granted ->
                    if (granted) {
                        onResult(true)
                    } else {
                        // In iOS, show settings dialog directly after first denial
                        isPermissionDeniedPermanently = true
                        showDialog = true
                    }
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                // Permission already denied - show settings dialog
                isPermissionDeniedPermanently = true
                showDialog = true
            }
        }
    }
}
```

#### iOS Permission Request Example

```kotlin
// Example: Requesting camera permission in iOS
fun requestCameraAccess(callback: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        callback(granted)
    }
}

// Check current permission status
val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
when (status) {
    AVAuthorizationStatusAuthorized -> {
        // Permission already granted
        startCamera()
    }
    AVAuthorizationStatusNotDetermined -> {
        // Request permission
        requestCameraAccess { granted ->
            if (granted) {
                startCamera()
            } else {
                showSettingsDialog()
            }
        }
    }
    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
        // Permission denied - show settings dialog
        showSettingsDialog()
    }
}
```

## User Experience Flow

### Android User Experience

1. **First Time Request**
   ```
   App requests camera permission
   ↓
   System shows permission dialog
   ↓
   User grants permission → Camera starts
   User denies permission → Show retry dialog
   ```

2. **Retry Dialog**
   ```
   Show "Grant Permission" dialog
   ↓
   User clicks "Grant Permission"
   ↓
   System shows permission dialog again
   ↓
   User grants → Camera starts
   User denies → Mark as permanently denied
   ```

3. **Permanently Denied**
   ```
   Show "Open Settings" dialog
   ↓
   User clicks "Open Settings"
   ↓
   App opens system settings
   ↓
   User enables permission manually
   ↓
   Return to app → Camera starts
   ```

### iOS User Experience

1. **First Time Request**
   ```
   App requests camera permission
   ↓
   System shows permission dialog
   ↓
   User grants permission → Camera starts
   User denies permission → Show settings dialog
   ```

2. **Settings Dialog**
   ```
   Show "Open Settings" dialog
   ↓
   User clicks "Open Settings"
   ↓
   App opens system settings
   ↓
   User enables permission manually
   ↓
   Return to app → Camera starts
   ```

## Dialog Implementation

### Android Permission Dialogs

```kotlin
// Retry Dialog
CustomPermissionDialog(
    title = "Camera permission denied",
    description = "Camera permission is required to capture photos. Please grant the permissions",
    confirmationButtonText = "Grant permission",
    onConfirm = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
)

// Settings Dialog
CustomPermissionDialog(
    title = "Camera permission required",
    description = "Camera permission is required to capture photos. Please grant it in settings",
    confirmationButtonText = "Open settings",
    onConfirm = {
        openAppSettings(context)
    }
)
```

### iOS Permission Dialogs

```kotlin
// Settings Dialog (only dialog shown in iOS)
CustomPermissionDialog(
    title = "Camera permission required",
    description = "Camera permission is required to capture photos. Please grant it in settings",
    confirmationButtonText = "Open settings",
    onConfirm = {
        openSettings()
    }
)
```

## Settings Navigation

### Android Settings Navigation

```kotlin
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
```

### iOS Settings Navigation

```kotlin
fun openSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
        UIApplication.sharedApplication.openURL(settingsUrl)
    }
}
```

## Error Handling

### Android Error Handling

```kotlin
// Handle permission errors
when {
    permissionDeniedCount >= 2 -> {
        onError(PhotoCaptureException("Camera permission permanently denied"))
    }
    else -> {
        // Show retry dialog
        showRationale = true
    }
}
```

### iOS Error Handling

```kotlin
// Handle permission errors
when (currentStatus) {
    AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
        onError(PhotoCaptureException("Camera permission denied"))
    }
    else -> {
        // Handle other cases
    }
}
```

## Best Practices

### Android Best Practices

1. **Request permissions at the right time**
   ```kotlin
   // Request permission when user actually needs camera
   LaunchedEffect(Unit) {
       if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
           == PackageManager.PERMISSION_GRANTED) {
           onResult(true)
       } else {
           permissionLauncher.launch(Manifest.permission.CAMERA)
       }
   }
   ```

2. **Provide clear rationale**
   ```kotlin
   // Explain why permission is needed
   description = "Camera permission is required to capture photos for your profile"
   ```

3. **Handle all permission states**
   ```kotlin
   when (currentPermission) {
       PackageManager.PERMISSION_GRANTED -> onResult(true)
       PackageManager.PERMISSION_DENIED -> requestPermission()
   }
   ```

### iOS Best Practices

1. **Check permission status first**
   ```kotlin
   val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
   when (status) {
       AVAuthorizationStatusAuthorized -> startCamera()
       AVAuthorizationStatusNotDetermined -> requestPermission()
       else -> showSettingsDialog()
   }
   ```

2. **Provide clear instructions**
   ```kotlin
   // Clear instructions for settings
   description = "Please go to Settings > Privacy & Security > Camera and enable access for this app"
   ```

3. **Handle restricted permissions**
   ```kotlin
   // Check for parental restrictions
   if (status == AVAuthorizationStatusRestricted) {
       showParentalControlDialog()
   }
   ```

## Testing Scenarios

### Android Testing

1. **First time permission request**
   - Grant permission → Should start camera
   - Deny permission → Should show retry dialog

2. **Retry permission request**
   - Grant permission → Should start camera
   - Deny permission → Should show settings dialog

3. **Settings navigation**
   - Click "Open Settings" → Should open app settings
   - Enable permission → Should start camera when returning

### iOS Testing

1. **First time permission request**
   - Grant permission → Should start camera
   - Deny permission → Should show settings dialog

2. **Settings navigation**
   - Click "Open Settings" → Should open system settings
   - Enable permission → Should start camera when returning

3. **Already denied permission**
   - Open app → Should show settings dialog directly

## Common Issues and Solutions

### Android Issues

1. **Permission not requested**
   ```kotlin
   // Solution: Check if permission is already granted
   if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
       != PackageManager.PERMISSION_GRANTED) {
       permissionLauncher.launch(Manifest.permission.CAMERA)
   }
   ```

2. **Multiple permission requests**
   ```kotlin
   // Solution: Use permission counter
   var permissionDeniedCount by remember { mutableIntStateOf(0) }
   ```

### iOS Issues

1. **Permission status not updated**
   ```kotlin
   // Solution: Check status after returning from settings
   LaunchedEffect(Unit) {
       val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
       if (status == AVAuthorizationStatusAuthorized) {
           onResult(true)
       }
   }
   ```

2. **Settings not opening**
   ```kotlin
   // Solution: Check if URL can be opened
   if (UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
       UIApplication.sharedApplication.openURL(settingsUrl)
   }
   ```

## Summary

The permission handling system in ImagePickerKMP provides a consistent experience across platforms while respecting the native behavior of each operating system:

- **Android**: Allows retries with rationale dialogs
- **iOS**: Shows settings dialog directly after first denial
- **Both**: Provide clear user guidance and error handling
- **Both**: Support custom dialogs and callbacks

This implementation ensures that users understand why permissions are needed and how to enable them if initially denied. 