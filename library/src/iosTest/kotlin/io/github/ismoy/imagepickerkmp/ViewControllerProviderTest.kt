package io.github.ismoy.imagepickerkmp

import io.github.ismoy.imagepickerkmp.domain.utils.ViewControllerProvider
import kotlin.test.Test
import kotlin.test.assertTrue

class ViewControllerProviderTest {
    
    @Test
    fun `ViewControllerProvider class should be accessible`() {
        assertTrue(ViewControllerProvider::class.simpleName == "ViewControllerProvider")
    }
}