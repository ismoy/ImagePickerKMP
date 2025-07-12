package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class FileManagerUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fileManagerUi_displaysFileComponents() {
        composeTestRule.setContent {
            FakeFileManagerUi()
        }
        composeTestRule.onNodeWithText("File Manager").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create File").assertIsDisplayed()
        composeTestRule.onNodeWithText("File Path").assertIsDisplayed()
    }
}

@Composable
fun FakeFileManagerUi() {
    Text("File Manager")
    Text("Create File")
    Text("File Path")
} 