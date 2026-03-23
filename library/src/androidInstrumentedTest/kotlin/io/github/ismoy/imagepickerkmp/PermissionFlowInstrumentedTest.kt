package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.ismoy.imagepickerkmp.presentation.ui.screens.CameraCaptureView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PermissionFlowInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun testPermissionDeniedFlow() {
        var errorCalled = false
        composeTestRule.setContent {
            CameraCaptureView(
                activity = composeTestRule.activity,
                onPhotoResult = { },
                onError = { errorCalled = true }
            )
        }
        // Here you should simulate permission denial using GrantPermissionRule or UI Automator
        composeTestRule.runOnIdle {
            assert(errorCalled)
        }
    }
} 