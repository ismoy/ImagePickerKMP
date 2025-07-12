package io.github.ismoy.imagepickerkmp

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstantTest {
    
    @Test
    fun testConstantValues() {
        // Test that the constant has the expected value
        assertEquals(0, Constant.PERMISSION_COUNTER)
    }
    
    @Test
    fun testConstantType() {
        // Test that the constant is an integer
        assertTrue(Constant.PERMISSION_COUNTER is Int)
    }
    
    @Test
    fun testConstantIsZero() {
        // Test that the constant is zero
        assertEquals(0, Constant.PERMISSION_COUNTER)
    }
} 