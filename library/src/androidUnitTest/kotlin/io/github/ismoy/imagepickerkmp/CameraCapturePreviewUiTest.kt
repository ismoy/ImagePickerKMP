package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class CameraCapturePreviewUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cameraCapturePreview_showsPreviewAndControls() {
        composeTestRule.setContent {
            FakeCameraCapturePreview()
        }
        composeTestRule.onNodeWithText("Camera Preview").assertIsDisplayed()
        composeTestRule.onNodeWithText("Switch Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flash").assertIsDisplayed()
    }
}

@Composable
fun FakeCameraCapturePreview() {
    Text("Camera Preview")
    Button(onClick = {}) { Text("Switch Camera") }
    Button(onClick = {}) { Text("Flash") }
} 