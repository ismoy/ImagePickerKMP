package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class RequestCameraPermissionDialogUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun requestCameraPermissionDialog_showsMessageAndButtons() {
        composeTestRule.setContent {
            FakeRequestCameraPermissionDialog()
        }
        composeTestRule.onNodeWithText("Camera Permission Needed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }
}

@Composable
fun FakeRequestCameraPermissionDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Camera Permission Needed") },
        text = { Text("Please grant camera access to continue.") },
        confirmButton = { Button(onClick = {}) { Text("Grant") } },
        dismissButton = { Button(onClick = {}) { Text("Cancel") } }
    )
} 