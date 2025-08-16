package io.github.ismoy.imagepickerkmp

import org.junit.Test
import org.junit.Assert.*

class SimpleTest {
    
    @Test
    fun `simple test that should always pass`() {
        assertTrue("This test should always pass", true)
        assertEquals("Basic assertion", 2 + 2, 4)
    }
    
    @Test
    fun `another simple test`() {
        val result = "Hello".length
        assertEquals(5, result)
    }
}
