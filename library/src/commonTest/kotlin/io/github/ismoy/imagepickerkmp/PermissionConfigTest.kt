package io.github.ismoy.imagepickerkmp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PermissionConfigTest {
    
    @Test
    fun `test permission config creation with default values`() {
        val config = PermissionConfig()
        
        assertNotNull(config.titleDialogConfig)
        assertNotNull(config.descriptionDialogConfig)
        assertNotNull(config.btnDialogConfig)
        assertNotNull(config.titleDialogDenied)
        assertNotNull(config.descriptionDialogDenied)
        assertNotNull(config.btnDialogDenied)
    }
    
    @Test
    fun `test permission config creation with custom values`() {
        val customTitle = "Custom Title"
        val customDescription = "Custom Description"
        val customButton = "Custom Button"
        
        val config = PermissionConfig(
            titleDialogConfig = customTitle,
            descriptionDialogConfig = customDescription,
            btnDialogConfig = customButton
        )
        
        assertEquals(customTitle, config.titleDialogConfig)
        assertEquals(customDescription, config.descriptionDialogConfig)
        assertEquals(customButton, config.btnDialogConfig)
    }
    
    @Test
    fun `test permission config data class properties`() {
        val config = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description",
            btnDialogConfig = "Test Button",
            titleDialogDenied = "Denied Title",
            descriptionDialogDenied = "Denied Description",
            btnDialogDenied = "Denied Button"
        )
        
        assertEquals("Test Title", config.titleDialogConfig)
        assertEquals("Test Description", config.descriptionDialogConfig)
        assertEquals("Test Button", config.btnDialogConfig)
        assertEquals("Denied Title", config.titleDialogDenied)
        assertEquals("Denied Description", config.descriptionDialogDenied)
        assertEquals("Denied Button", config.btnDialogDenied)
    }
    
    @Test
    fun `test permission config copy functionality`() {
        val originalConfig = PermissionConfig(
            titleDialogConfig = "Original Title",
            descriptionDialogConfig = "Original Description"
        )
        
        val copiedConfig = originalConfig.copy(
            titleDialogConfig = "Modified Title"
        )
        
        assertEquals("Modified Title", copiedConfig.titleDialogConfig)
        assertEquals("Original Description", copiedConfig.descriptionDialogConfig)
    }
    
    @Test
    fun `test permission config default values are not empty`() {
        val config = PermissionConfig()
        
        assertTrue(config.titleDialogConfig.isNotEmpty())
        assertTrue(config.descriptionDialogConfig.isNotEmpty())
        assertTrue(config.btnDialogConfig.isNotEmpty())
        assertTrue(config.titleDialogDenied.isNotEmpty())
        assertTrue(config.descriptionDialogDenied.isNotEmpty())
        assertTrue(config.btnDialogDenied.isNotEmpty())
    }
    
    @Test
    fun `test permission config equality`() {
        val config1 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        val config2 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        assertEquals(config1, config2)
    }
    
    @Test
    fun `test permission config hash code consistency`() {
        val config1 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        val config2 = PermissionConfig(
            titleDialogConfig = "Test Title",
            descriptionDialogConfig = "Test Description"
        )
        
        assertEquals(config1.hashCode(), config2.hashCode())
    }
} 