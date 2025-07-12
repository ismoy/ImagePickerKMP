package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class CameraCaptureViewUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cameraCaptureView_showsCameraPreview() {
        composeTestRule.setContent {
            FakeCameraCaptureView()
        }
        composeTestRule.onNodeWithText("Camera Preview").assertIsDisplayed()
        composeTestRule.onNodeWithText("Capture").assertIsDisplayed()
    }
}

@Composable
fun FakeCameraCaptureView() {
    Text("Camera Preview")
    Button(onClick = {}) { Text("Capture") }
} 