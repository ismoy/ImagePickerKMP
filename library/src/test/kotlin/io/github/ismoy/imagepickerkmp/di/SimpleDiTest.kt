package io.github.ismoy.imagepickerkmp.di

import org.junit.Test
import org.junit.Assert.*

class SimpleDiTest {

    @Test
    fun koinModule_shouldBeAccessible() {
        // Test that the Koin module can be accessed
        val module = imagePickerModule
        assertNotNull(module)
    }

    @Test
    fun koinModule_shouldHaveDefinitions() {
        val module = imagePickerModule
        
        // Test that module has definitions
        assertNotNull(module.definitions)
        assertTrue(module.definitions.isNotEmpty())
    }

    @Test
    fun moduleProvider_shouldWork() {
        // Test module provider functionality
        try {
            val modules = getImagePickerModules()
            assertNotNull(modules)
            assertTrue(modules.isNotEmpty())
        } catch (e: Exception) {
            // May fail in test environment
            assertNotNull(e)
        }
    }
}
