package io.github.ismoy.imagepickerkmp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        // Aquí deberías simular la denegación de permisos usando GrantPermissionRule o UI Automator
        // Este paso depende del entorno y configuración de test
        // Por ahora, solo estructura el test para que puedas completarlo según tu flujo real
        composeTestRule.runOnIdle {
            // Verifica que el callback de error fue llamado
            assert(errorCalled)
        }
    }
} 