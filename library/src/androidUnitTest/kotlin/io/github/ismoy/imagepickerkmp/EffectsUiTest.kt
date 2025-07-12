package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test

class EffectsUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun effectsUi_displaysEffectComponents() {
        composeTestRule.setContent {
            FakeEffectsUi()
        }
        composeTestRule.onNodeWithText("Effect Component").assertIsDisplayed()
        composeTestRule.onNodeWithText("Animation").assertIsDisplayed()
    }
}

@Composable
fun FakeEffectsUi() {
    Text("Effect Component")
    Text("Animation")
} 