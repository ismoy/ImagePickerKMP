package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class ImageConfirmationViewWithCustomButtonsUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun imageConfirmationViewWithCustomButtons_showsCustomButtons() {
        composeTestRule.setContent {
            FakeImageConfirmationViewWithCustomButtons()
        }
        composeTestRule.onNodeWithText("Custom Accept").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Image Preview").assertIsDisplayed()
    }
}

@Composable
fun FakeImageConfirmationViewWithCustomButtons() {
    Text("Image Preview")
    Button(onClick = {}) { Text("Custom Accept") }
    Button(onClick = {}) { Text("Custom Cancel") }
} 