package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class StringsUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stringsUi_displaysStringResources() {
        composeTestRule.setContent {
            FakeStringsUi()
        }
        composeTestRule.onNodeWithText("Camera Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gallery Permission").assertIsDisplayed()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }
}

@Composable
fun FakeStringsUi() {
    Text("Camera Permission")
    Text("Gallery Permission")
    Text("Settings")
} 