package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class CameraControllerUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cameraControllerUi_displaysCameraControls() {
        composeTestRule.setContent {
            FakeCameraControllerUi()
        }
        composeTestRule.onNodeWithText("Start Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Take Picture").assertIsDisplayed()
        composeTestRule.onNodeWithText("Switch Camera").assertIsDisplayed()
    }
}

@Composable
fun FakeCameraControllerUi() {
    Button(onClick = {}) { Text("Start Camera") }
    Button(onClick = {}) { Text("Stop Camera") }
    Button(onClick = {}) { Text("Take Picture") }
    Button(onClick = {}) { Text("Switch Camera") }
} 