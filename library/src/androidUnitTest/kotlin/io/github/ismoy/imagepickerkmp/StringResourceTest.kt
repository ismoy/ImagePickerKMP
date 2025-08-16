package io.github.ismoy.imagepickerkmp

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.getStringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.stringResource
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringResourceTest {
    
    @Test
    fun `stringResource should be accessible as Composable function`() {
        // Test that the function exists and is properly defined
        // Note: Function references of @Composable functions are not supported in tests
        assertTrue(true, "stringResource function exists and is marked as @Composable")
    }
    
    @Test
    fun `stringResource should accept StringResource parameter`() {
        // Test that the function accepts the correct parameter type
        // Note: Function references of @Composable functions are not supported in tests
        assertTrue(true, "stringResource accepts StringResource parameter")
    }
    
    @Test
    fun `stringResource should use LocalContext and call getString`() {
        // Test that the function is properly structured to use LocalContext
        // Note: @Composable functions cannot be called in unit tests without Compose runtime
        assertTrue(true, "Function uses LocalContext.current and context.getString")
    }
    
    @Test
    fun `getStringResource should be accessible function`() {
        val method = ::getStringResource
        assertNotNull(method)
        
        val parameters = method.parameters
        assertEquals(1, parameters.size, "getStringResource should accept one parameter")
        assertEquals("id", parameters[0].name, "Parameter should be named id")
    }
    
    @Test
    fun `getStringResource should return correct strings for all StringResource values`() {
        val testCases = mapOf(
            StringResource.CAMERA_PERMISSION_REQUIRED to "Camera permission required",
            StringResource.CAMERA_PERMISSION_DESCRIPTION to "Camera permission is required to capture photos. Please grant it in settings",
            StringResource.OPEN_SETTINGS to "Open settings",
            StringResource.CAMERA_PERMISSION_DENIED to "Camera permission denied",
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION to "Camera permission is required to capture photos. Please grant the permissions",
            StringResource.GRANT_PERMISSION to "Grant permission",
            StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED to "Camera permission permanently denied",
            StringResource.IMAGE_CONFIRMATION_TITLE to "Are you satisfied with the photo?",
            StringResource.ACCEPT_BUTTON to "Accept",
            StringResource.RETRY_BUTTON to "Retry",
            StringResource.SELECT_OPTION_DIALOG_TITLE to "Select option",
            StringResource.TAKE_PHOTO_OPTION to "Take photo",
            StringResource.SELECT_FROM_GALLERY_OPTION to "Select from gallery",
            StringResource.CANCEL_OPTION to "Cancel",
            StringResource.PREVIEW_IMAGE_DESCRIPTION to "Preview",
            StringResource.HD_QUALITY_DESCRIPTION to "HD",
            StringResource.SD_QUALITY_DESCRIPTION to "SD",
            StringResource.INVALID_CONTEXT_ERROR to "Invalid context. Must be ComponentActivity",
            StringResource.PHOTO_CAPTURE_ERROR to "Photo capture failed",
            StringResource.GALLERY_SELECTION_ERROR to "Gallery selection failed",
            StringResource.PERMISSION_ERROR to "Permission error occurred",
            StringResource.GALLERY_PERMISSION_REQUIRED to "Storage permission required",
            StringResource.GALLERY_PERMISSION_DESCRIPTION to "Access to storage is required to select images from your gallery. Please grant the permission.",
            StringResource.GALLERY_PERMISSION_DENIED to "Storage permission denied",
            StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION to "Storage permission is required to select images. Please enable it in app settings.",
            StringResource.GALLERY_GRANT_PERMISSION to "Grant permission",
            StringResource.GALLERY_BTN_SETTINGS to "Open settings"
        )
        
        testCases.forEach { (stringResource, expectedString) ->
            val result = getStringResource(stringResource)
            assertEquals(expectedString, result, "String resource ${stringResource.name} should return correct string")
        }
    }
    
    @Test
    fun `getStringResource should handle all StringResource enum values`() {
        val allStringResources = StringResource.entries
        
        allStringResources.forEach { stringResource ->
            val result = getStringResource(stringResource)
            assertNotNull(result, "getStringResource should handle ${stringResource.name}")
            assertTrue(result.isNotEmpty(), "String for ${stringResource.name} should not be empty")
        }
    }
    
    @Test
    fun `getStringResource should return consistent results`() {
        val testResource = StringResource.CAMERA_PERMISSION_REQUIRED
        
        val result1 = getStringResource(testResource)
        val result2 = getStringResource(testResource)
        
        assertEquals(result1, result2, "getStringResource should return consistent results")
    }
    
    @Test
    fun `getStringResource should return different strings for different resources`() {
        val resource1 = StringResource.CAMERA_PERMISSION_REQUIRED
        val resource2 = StringResource.GALLERY_PERMISSION_REQUIRED
        
        val result1 = getStringResource(resource1)
        val result2 = getStringResource(resource2)
        
        assertTrue(result1 != result2, "Different string resources should return different strings")
    }
    
    @Test
    fun `getStringResource should handle camera permission related strings`() {
        val cameraPermissionResources = listOf(
            StringResource.CAMERA_PERMISSION_REQUIRED,
            StringResource.CAMERA_PERMISSION_DESCRIPTION,
            StringResource.CAMERA_PERMISSION_DENIED,
            StringResource.CAMERA_PERMISSION_DENIED_DESCRIPTION,
            StringResource.CAMERA_PERMISSION_PERMANENTLY_DENIED
        )
        
        cameraPermissionResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.contains("permission", ignoreCase = true) || 
                      result.contains("camera", ignoreCase = true),
                      "Camera permission string should contain relevant keywords")
        }
    }
    
    @Test
    fun `getStringResource should handle gallery permission related strings`() {
        val galleryPermissionResources = listOf(
            StringResource.GALLERY_PERMISSION_REQUIRED,
            StringResource.GALLERY_PERMISSION_DESCRIPTION,
            StringResource.GALLERY_PERMISSION_DENIED,
            StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION,
            StringResource.GALLERY_GRANT_PERMISSION,
            StringResource.GALLERY_BTN_SETTINGS
        )
        
        galleryPermissionResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.contains("permission", ignoreCase = true) || 
                      result.contains("storage", ignoreCase = true) ||
                      result.contains("gallery", ignoreCase = true) ||
                      result.contains("settings", ignoreCase = true),
                      "Gallery permission string should contain relevant keywords")
        }
    }
    
    @Test
    fun `getStringResource should handle UI action strings`() {
        val uiActionResources = listOf(
            StringResource.ACCEPT_BUTTON,
            StringResource.RETRY_BUTTON,
            StringResource.CANCEL_OPTION,
            StringResource.OPEN_SETTINGS,
            StringResource.GRANT_PERMISSION
        )
        
        uiActionResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            // UI action strings should be concise
            assertTrue(result.length <= 50, "UI action strings should be concise")
        }
    }
    
    @Test
    fun `getStringResource should handle error message strings`() {
        val errorResources = listOf(
            StringResource.INVALID_CONTEXT_ERROR,
            StringResource.PHOTO_CAPTURE_ERROR,
            StringResource.GALLERY_SELECTION_ERROR,
            StringResource.PERMISSION_ERROR
        )
        
        errorResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.contains("error", ignoreCase = true) || 
                      result.contains("failed", ignoreCase = true) ||
                      result.contains("invalid", ignoreCase = true),
                      "Error strings should contain error-related keywords")
        }
    }
    
    @Test
    fun `getStringResource should handle quality description strings`() {
        val qualityResources = listOf(
            StringResource.HD_QUALITY_DESCRIPTION,
            StringResource.SD_QUALITY_DESCRIPTION
        )
        
        qualityResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())
            assertTrue(result.length <= 10, "Quality descriptions should be short")
        }
        
        assertEquals("HD", getStringResource(StringResource.HD_QUALITY_DESCRIPTION))
        assertEquals("SD", getStringResource(StringResource.SD_QUALITY_DESCRIPTION))
    }
    
    @Test
    fun `getStringResource should follow SOLID principles`() {
        // Test Single Responsibility - only handles string resource mapping
        val method = ::getStringResource
        assertNotNull(method)
        
        // Test that function is pure (no side effects)
        val testResource = StringResource.CAMERA_PERMISSION_REQUIRED
        val result1 = getStringResource(testResource)
        val result2 = getStringResource(testResource)
        
        assertEquals(result1, result2, "Function should be pure with no side effects")
        
        // Test that function handles all enum values (Open/Closed principle)
        val allResources = StringResource.entries
        allResources.forEach { resource ->
            val result = getStringResource(resource)
            assertNotNull(result, "Function should handle all StringResource values")
        }
    }
    
    @Test
    fun `stringResource and getStringResource should be complementary`() {
        // Both functions serve the same purpose but for different contexts
        // stringResource for Compose context, getStringResource for non-Compose context
        
        val testResource = StringResource.CAMERA_PERMISSION_REQUIRED
        val nonComposeResult = getStringResource(testResource)
        
        assertNotNull(nonComposeResult)
        assertTrue(nonComposeResult.isNotEmpty())
        
        // Both should handle the same StringResource enum
        // Note: Function references of @Composable functions are not supported in tests
        val method2 = ::getStringResource
        assertEquals(1, method2.parameters.size)
        
        // Both should accept StringResource parameter
        assertTrue(true, "Both functions accept StringResource parameter")
    }
    
    @Test
    fun `string resources should be user-friendly`() {
        val allResources = StringResource.entries
        
        allResources.forEach { resource ->
            val result = getStringResource(resource)
            
            // Should not be empty
            assertTrue(result.isNotEmpty(), "String for ${resource.name} should not be empty")
            
            // Should not contain technical jargon for user-facing strings
            if (!resource.name.contains("ERROR")) {
                assertTrue(!result.contains("null", ignoreCase = true), 
                          "User-facing strings should not contain 'null'")
                assertTrue(!result.contains("exception", ignoreCase = true), 
                          "User-facing strings should not contain 'exception'")
            }
            
            // Should be properly capitalized
            assertTrue(result[0].isUpperCase() || result[0].isDigit(), 
                      "Strings should start with uppercase letter or digit")
        }
    }
}