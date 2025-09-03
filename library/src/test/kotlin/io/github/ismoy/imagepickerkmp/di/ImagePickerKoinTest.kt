package io.github.ismoy.imagepickerkmp.di

import io.github.ismoy.imagepickerkmp.domain.utils.DefaultLogger
import io.github.ismoy.imagepickerkmp.domain.utils.ImagePickerLogger
import io.github.ismoy.imagepickerkmp.presentation.viewModel.ImagePickerViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ImagePickerKoinTest {

    @Test
    fun testImagePickerCommonModuleExists() {
        assertNotNull("Common module should exist", imagePickerCommonModule)
    }

    @Test
    fun testInitImagePickerKoinExists() {
        // Test that the function exists and can be called
        assertNotNull("initImagePickerKoin function should exist", ::initImagePickerKoin)
    }

    @Test
    fun testInitImagePickerKoinWithEmptyPlatformModules() {
        try {
            // Arrange
            stopKoin() // Clean up any existing Koin instance

            // Act
            val koin = initImagePickerKoin()

            // Assert
            assertNotNull("Koin instance should be created", koin)
            assertNotNull("GlobalContext should be available", GlobalContext.getOrNull())

        } finally {
            stopKoin() // Clean up
        }
    }

    @Test
    fun testInitImagePickerKoinWithPlatformModules() {
        try {
            // Arrange
            stopKoin()
            val testModule = module {
                single<String> { "test_dependency" }
            }

            // Act
            val koin = initImagePickerKoin(listOf(testModule))

            // Assert
            assertNotNull("Koin instance should be created", koin)

        } finally {
            stopKoin()
        }
    }

    @Test
    fun testKoinAppDeclarationParameter() {
        try {
            // Arrange
            stopKoin()
            var declarationCalled = false

            // Act
            initImagePickerKoin(appDeclaration = {
                declarationCalled = true
            })

            // Assert
            assertTrue("App declaration should be called", declarationCalled)

        } finally {
            stopKoin()
        }
    }

    @Test
    fun testModuleListBuilding() {
        try {
            // Arrange
            stopKoin()
            val platformModule1 = module { single<Int> { 42 } }
            val platformModule2 = module { single<Boolean> { true } }

            // Act
            initImagePickerKoin(listOf(platformModule1, platformModule2))

            // Assert - Just test that it doesn't throw
            assertNotNull("Koin should be initialized", GlobalContext.getOrNull())

        } finally {
            stopKoin()
        }
    }

    @Test
    fun testMultipleInitCalls() {
        try {
            // Arrange
            stopKoin()

            // Act - First init call
            val koin1 = initImagePickerKoin()
            assertNotNull("First Koin should not be null", koin1)
            
            // Stop Koin before second init call
            stopKoin()
            
            // Second init call after stopping
            val koin2 = initImagePickerKoin()
            assertNotNull("Second Koin should not be null", koin2)

        } finally {
            stopKoin()
        }
    }

    @Test
    fun testEmptyPlatformModulesList() {
        try {
            // Arrange
            stopKoin()

            // Act
            val koin = initImagePickerKoin(emptyList())

            // Assert
            assertNotNull("Koin should be created with empty platform modules", koin)

        } finally {
            stopKoin()
        }
    }

    @Test 
    fun testCommonModuleCreation() {
        // Test that the module can be created
        assertNotNull("Common module should not be null", imagePickerCommonModule)
        
        // Test module properties
        assertTrue("Module should be a Module", imagePickerCommonModule is org.koin.core.module.Module)
    }

    @Test
    fun testInitFunctionSignature() {
        // Test function parameters and return type
        val function = ::initImagePickerKoin
        assertNotNull("Function should exist", function)
        
        // Test that function can be called with different parameter combinations
        try {
            stopKoin()
            
            // Call with no parameters
            initImagePickerKoin()
            stopKoin()
            
            // Call with empty list
            initImagePickerKoin(emptyList())
            stopKoin()
            
            // Call with custom declaration
            initImagePickerKoin { }
            
        } finally {
            stopKoin()
        }
    }

    @Test
    fun testKoinContextManagement() {
        try {
            // Start with clean state
            stopKoin()
            assertNull("Context should be null initially", GlobalContext.getOrNull())
            
            // Initialize Koin
            initImagePickerKoin()
            assertNotNull("Context should exist after init", GlobalContext.getOrNull())
            
            // Stop Koin
            stopKoin()
            assertNull("Context should be null after stop", GlobalContext.getOrNull())
            
        } finally {
            stopKoin()
        }
    }
}
