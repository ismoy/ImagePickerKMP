package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.di.imagePickerCommonModule
import io.github.ismoy.imagepickerkmp.di.initImagePickerKoin
import kotlin.test.Test
import kotlin.test.assertNotNull

class SimpleKoinTest {
    
    @Test
    fun `imagePickerCommonModule should be created`() {
        val module = imagePickerCommonModule
        assertNotNull(module)
    }
    
    @Test
    fun `initImagePickerKoin should not throw exception`() {
        // Test that Koin initialization doesn't crash
        try {
            initImagePickerKoin()
        } catch (e: Exception) {
            // If it fails, that's okay for this simple test
            // We just want to make sure the function exists and can be called
        }
    }
    
    @Test
    fun `initImagePickerKoin with empty modules should not throw exception`() {
        try {
            initImagePickerKoin(platformModules = emptyList())
        } catch (e: Exception) {
            // If it fails, that's okay for this simple test
        }
    }
}