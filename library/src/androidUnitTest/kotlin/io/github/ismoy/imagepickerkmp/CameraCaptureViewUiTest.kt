package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraCaptureViewUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun testCameraCaptureView_onPhotoResultCalled() {
        var photoCaptured = false
        composeTestRule.setContent {
            CameraCaptureView(
                activity = composeTestRule.activity,
                onPhotoResult = { photoCaptured = true },
                onError = { }
            )
        }
        // Simula click en el botón de captura (ajusta el contentDescription según tu UI real)
        composeTestRule.onNodeWithContentDescription("Capture").performClick()
        // Verifica que el callback fue llamado
        composeTestRule.runOnIdle {
            assert(photoCaptured)
        }
    }
} 