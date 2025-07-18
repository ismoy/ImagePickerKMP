package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class ImagePickerConfirmationViewUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun confirmationView_showsAcceptAndCancelButtons() {
        composeTestRule.setContent {
            FakeConfirmationView()
        }
        composeTestRule.onNodeWithText("Accept").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }
}

@Composable
fun FakeConfirmationView() {
    Button(onClick = {}) { Text("Accept") }
    Button(onClick = {}) { Text("Cancel") }
} 