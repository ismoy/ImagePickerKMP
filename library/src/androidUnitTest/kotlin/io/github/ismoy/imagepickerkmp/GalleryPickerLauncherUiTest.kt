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

class GalleryPickerLauncherUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun galleryPickerLauncher_showsLauncherButton() {
        composeTestRule.setContent {
            FakeGalleryPickerLauncher()
        }
        composeTestRule.onNodeWithText("Pick from Gallery").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pick from Gallery").performClick()
    }
}

@Composable
fun FakeGalleryPickerLauncher() {
    Button(onClick = {}) { Text("Pick from Gallery") }
} 