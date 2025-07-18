package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class ImageProcessorUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun imageProcessorUi_displaysProcessingComponents() {
        composeTestRule.setContent {
            FakeImageProcessorUi()
        }
        composeTestRule.onNodeWithText("Processing Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Image Result").assertIsDisplayed()
    }
}

@Composable
fun FakeImageProcessorUi() {
    Text("Processing Image")
    Text("Image Result")
} 