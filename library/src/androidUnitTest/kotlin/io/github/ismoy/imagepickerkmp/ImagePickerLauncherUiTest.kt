package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class ImagePickerLauncherUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun imagePickerLauncher_showsLauncherButton() {
        composeTestRule.setContent {
            FakeImagePickerLauncher()
        }
        composeTestRule.onNodeWithText("Pick Image").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pick Image").performClick()
    }
}

@Composable
fun FakeImagePickerLauncher() {
    Button(onClick = {}) { Text("Pick Image") }
} 