# Analytics Guide

This guide explains how to integrate analytics with ImagePickerKMP to track user interactions and app performance.

## Table of Contents

- [Overview](#overview)
- [Firebase Analytics Setup](#firebase-analytics-setup)
- [Custom Analytics](#custom-analytics)
- [Privacy Considerations](#privacy-considerations)
- [Implementation Examples](#implementation-examples)
- [Data Collection](#data-collection)
- [Troubleshooting](#troubleshooting)

## Overview

ImagePickerKMP provides optional analytics integration to help you understand how users interact with the camera functionality. The analytics system is:

- **Privacy-focused**: No personal data is collected
- **Opt-in**: Disabled by default
- **Configurable**: You control what data is collected
- **Cross-platform**: Works on both Android and iOS

## Firebase Analytics Setup

### 1. Add Firebase Dependencies

#### Android

In your `build.gradle.kts` (app level):

```kotlin
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
}
```

#### iOS

In your `Podfile`:

```ruby
target 'YourApp' do
  use_frameworks!
  pod 'Firebase/Analytics'
end
```

### 2. Initialize Firebase

#### Android

In your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
```

#### iOS

In your `AppDelegate.swift`:

```swift
import Firebase

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        return true
    }
}
```

### 3. Configure Analytics in ImagePickerKMP

```kotlin
@Composable
fun ImagePickerWithAnalytics() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Track successful photo capture
            FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                putString("photo_uri", result.uri.toString())
                putLong("photo_size", result.size)
                putString("photo_format", result.format)
            })
        },
        onError = { exception ->
            // Track errors
            FirebaseAnalytics.getInstance(context).logEvent("photo_capture_error", Bundle().apply {
                putString("error_type", exception.javaClass.simpleName)
                putString("error_message", exception.message)
            })
        }
    )
}
```

## Custom Analytics

### 1. Create Custom Analytics Implementation

```kotlin
class CustomAnalytics {
    fun trackPhotoCapture(uri: Uri, size: Long, format: String) {
        // Your custom analytics implementation
        println("Photo captured: $uri, size: $size, format: $format")
    }
    
    fun trackPermissionRequest(granted: Boolean) {
        // Track permission requests
        println("Permission requested: ${if (granted) "granted" else "denied"}")
    }
    
    fun trackError(error: Exception) {
        // Track errors
        println("Error occurred: ${error.message}")
    }
}
```

### 2. Use Custom Analytics with ImagePickerKMP

```kotlin
@Composable
fun ImagePickerWithCustomAnalytics() {
    val analytics = remember { CustomAnalytics() }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            analytics.trackPhotoCapture(result.uri, result.size, result.format)
        },
        onError = { exception ->
            analytics.trackError(exception)
        }
    )
}
```

## Privacy Considerations

### 1. Data Collection Policy

ImagePickerKMP analytics collect only anonymous, non-personal data:

- **Performance metrics**: Camera startup time, photo capture time
- **Usage statistics**: Number of photos captured, error rates
- **System information**: Platform, OS version, device type
- **No personal data**: No user identifiers, no photo content

### 2. Opt-out Configuration

```kotlin
@Composable
fun ImagePickerWithoutAnalytics() {
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            // Handle photo capture without analytics
        },
        onError = { exception ->
            // Handle errors without analytics
        }
        // Analytics are disabled by default
    )
}
```

### 3. GDPR Compliance

```kotlin
@Composable
fun GDPRCompliantImagePicker() {
    var analyticsEnabled by remember { mutableStateOf(false) }
    
    // Show consent dialog
    if (!analyticsEnabled) {
        ConsentDialog(
            onAccept = { analyticsEnabled = true },
            onDecline = { analyticsEnabled = false }
        )
    }
    
    if (analyticsEnabled) {
        ImagePickerWithAnalytics()
    } else {
        ImagePickerWithoutAnalytics()
    }
}
```

## Implementation Examples

### 1. Basic Analytics Integration

```kotlin
@Composable
fun BasicAnalyticsExample() {
    var showImagePicker by remember { mutableStateOf(false) }
    
    if (showImagePicker) {
        ImagePickerLauncher(
            context = LocalContext.current,
            onPhotoCaptured = { result ->
                // Track basic metrics
                FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                    putString("timestamp", System.currentTimeMillis().toString())
                })
            },
            onError = { exception ->
                // Track errors
                FirebaseAnalytics.getInstance(context).logEvent("photo_error", Bundle().apply {
                    putString("error", exception.message)
                })
            }
        )
    }
    
    Button(onClick = { showImagePicker = true }) {
        Text("Take Photo")
    }
}
```

### 2. Advanced Analytics with Custom Events

```kotlin
@Composable
fun AdvancedAnalyticsExample() {
    val context = LocalContext.current
    val analytics = FirebaseAnalytics.getInstance(context)
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Track detailed photo capture event
            analytics.logEvent("photo_capture_success", Bundle().apply {
                putString("photo_id", result.uri.toString())
                putLong("file_size", result.size)
                putString("mime_type", result.format)
                putString("device_model", Build.MODEL)
                putString("os_version", Build.VERSION.RELEASE)
            })
        },
        onError = { exception ->
            // Track detailed error event
            analytics.logEvent("photo_capture_error", Bundle().apply {
                putString("error_type", exception.javaClass.simpleName)
                putString("error_message", exception.message)
                putString("device_model", Build.MODEL)
                putString("os_version", Build.VERSION.RELEASE)
            })
        }
    )
}
```

### 3. Performance Analytics

```kotlin
@Composable
fun PerformanceAnalyticsExample() {
    var startTime by remember { mutableStateOf(0L) }
    
    ImagePickerLauncher(
        context = LocalContext.current,
        onPhotoCaptured = { result ->
            val captureTime = System.currentTimeMillis() - startTime
            
            // Track performance metrics
            FirebaseAnalytics.getInstance(context).logEvent("photo_capture_performance", Bundle().apply {
                putLong("capture_time_ms", captureTime)
                putLong("photo_size_bytes", result.size)
            })
        },
        onError = { exception ->
            val errorTime = System.currentTimeMillis() - startTime
            
            // Track error performance
            FirebaseAnalytics.getInstance(context).logEvent("photo_capture_error_performance", Bundle().apply {
                putLong("time_to_error_ms", errorTime)
                putString("error_type", exception.javaClass.simpleName)
            })
        }
    )
    
    Button(onClick = { 
        startTime = System.currentTimeMillis()
        // Show image picker
    }) {
        Text("Take Photo")
    }
}
```

## Data Collection

### 1. What Data is Collected

#### Performance Metrics
- Camera startup time
- Photo capture time
- Error rates and types
- Memory usage

#### Usage Statistics
- Number of photos captured
- Permission request outcomes
- User interaction patterns
- Feature usage frequency

#### System Information
- Platform (Android/iOS)
- OS version
- Device type (phone/tablet)
- App version

### 2. Data Processing

```kotlin
class AnalyticsDataProcessor {
    fun processPhotoCaptureEvent(data: Bundle) {
        // Process and analyze photo capture data
        val captureTime = data.getLong("capture_time_ms", 0)
        val photoSize = data.getLong("photo_size_bytes", 0)
        
        // Send to analytics service
        sendToAnalytics("photo_capture", mapOf(
            "capture_time" to captureTime,
            "photo_size" to photoSize
        ))
    }
    
    fun processErrorEvent(data: Bundle) {
        // Process and analyze error data
        val errorType = data.getString("error_type", "unknown")
        val errorMessage = data.getString("error_message", "")
        
        // Send to analytics service
        sendToAnalytics("photo_capture_error", mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage
        ))
    }
}
```

### 3. Custom Data Collection

```kotlin
@Composable
fun CustomDataCollectionExample() {
    val context = LocalContext.current
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Collect custom data
            val customData = mapOf(
                "user_id" to getUserId(),
                "session_id" to getSessionId(),
                "photo_quality" to getPhotoQuality(),
                "camera_type" to getCameraType()
            )
            
            // Send to your analytics service
            sendToCustomAnalytics("photo_captured", customData)
        },
        onError = { exception ->
            // Collect custom error data
            val errorData = mapOf(
                "user_id" to getUserId(),
                "session_id" to getSessionId(),
                "error_type" to exception.javaClass.simpleName,
                "error_message" to exception.message
            )
            
            // Send to your analytics service
            sendToCustomAnalytics("photo_error", errorData)
        }
    )
}
```

## Troubleshooting

### 1. Common Analytics Issues

#### Firebase Not Initialized

**Problem**: Analytics events not being sent.

**Solution**: Ensure Firebase is properly initialized:

```kotlin
// Check if Firebase is initialized
if (FirebaseApp.getApps(context).isEmpty()) {
    FirebaseApp.initializeApp(context)
}
```

#### Events Not Appearing in Console

**Problem**: Events not showing up in Firebase Console.

**Solution**: Add debug logging:

```kotlin
// Enable debug mode
FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true)

// Add debug logging
FirebaseAnalytics.getInstance(context).logEvent("debug_test", Bundle().apply {
    putString("test", "value")
})
```

#### iOS Analytics Not Working

**Problem**: Analytics not working on iOS.

**Solution**: Check iOS configuration:

```swift
// In your iOS app
import Firebase

// Ensure Firebase is configured
if FirebaseApp.app() == nil {
    FirebaseApp.configure()
}

// Enable analytics collection
Analytics.setAnalyticsCollectionEnabled(true)
```

### 2. Debug Analytics

```kotlin
@Composable
fun DebugAnalyticsExample() {
    val context = LocalContext.current
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Debug logging
            Log.d("Analytics", "Photo captured: ${result.uri}")
            
            // Send analytics event
            FirebaseAnalytics.getInstance(context).logEvent("photo_captured", Bundle().apply {
                putString("debug_uri", result.uri.toString())
            })
        },
        onError = { exception ->
            // Debug logging
            Log.e("Analytics", "Error: ${exception.message}", exception)
            
            // Send analytics event
            FirebaseAnalytics.getInstance(context).logEvent("photo_error", Bundle().apply {
                putString("debug_error", exception.message)
            })
        }
    )
}
```

### 3. Testing Analytics

```kotlin
@Composable
fun TestAnalyticsExample() {
    val context = LocalContext.current
    
    // Test analytics events
    Button(onClick = {
        FirebaseAnalytics.getInstance(context).logEvent("test_event", Bundle().apply {
            putString("test_param", "test_value")
        })
    }) {
        Text("Test Analytics")
    }
    
    ImagePickerLauncher(
        context = context,
        onPhotoCaptured = { result ->
            // Test photo capture analytics
            FirebaseAnalytics.getInstance(context).logEvent("test_photo_capture", Bundle().apply {
                putString("test_photo_uri", result.uri.toString())
            })
        },
        onError = { exception ->
            // Test error analytics
            FirebaseAnalytics.getInstance(context).logEvent("test_photo_error", Bundle().apply {
                putString("test_error", exception.message)
            })
        }
    )
}
```

## Best Practices

### 1. Event Naming

```kotlin
// Use consistent event naming
val events = mapOf(
    "photo_captured" to "User captured a photo",
    "photo_capture_error" to "Error during photo capture",
    "permission_granted" to "Camera permission granted",
    "permission_denied" to "Camera permission denied"
)
```

### 2. Parameter Consistency

```kotlin
// Use consistent parameter names
val commonParams = mapOf(
    "user_id" to getUserId(),
    "session_id" to getSessionId(),
    "device_model" to Build.MODEL,
    "app_version" to BuildConfig.VERSION_NAME
)
```

### 3. Error Tracking

```kotlin
// Track errors with context
fun trackError(error: Exception, context: String) {
    FirebaseAnalytics.getInstance(context).logEvent("error", Bundle().apply {
        putString("error_type", error.javaClass.simpleName)
        putString("error_message", error.message)
        putString("error_context", context)
        putString("timestamp", System.currentTimeMillis().toString())
    })
}
```

## Support

For analytics-related issues:

1. **Check Firebase Console**: Verify events are being sent
2. **Enable Debug Mode**: Use Firebase debug mode for testing
3. **Review Logs**: Check Android/iOS logs for errors
4. **Contact Support**: Reach out for analytics-specific help

For more information, refer to:
- [Firebase Analytics Documentation](https://firebase.google.com/docs/analytics)
- [Privacy Guide](PRIVACY_GUIDE.md)
- [API Reference](API_REFERENCE.md) 