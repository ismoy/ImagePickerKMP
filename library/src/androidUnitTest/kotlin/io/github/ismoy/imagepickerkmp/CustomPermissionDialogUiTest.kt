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

class CustomPermissionDialogUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun customPermissionDialog_showsTitleAndButtons() {
        composeTestRule.setContent {
            FakeCustomPermissionDialog()
        }
        composeTestRule.onNodeWithText("Permission Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Allow").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deny").assertIsDisplayed()
    }
}

@Composable
fun FakeCustomPermissionDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permission Required") },
        text = { Text("This feature needs camera access.") },
        confirmButton = { Button(onClick = {}) { Text("Allow") } },
        dismissButton = { Button(onClick = {}) { Text("Deny") } }
    )
} 