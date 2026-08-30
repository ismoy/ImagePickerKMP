package io.github.ismoy.imagepickerkmp.scanner

import io.github.ismoy.imagepickerkmp.scanner.camera.config.ScannerCameraConfig
import io.github.ismoy.imagepickerkmp.scanner.domain.HapticFeedbackMode
import io.github.ismoy.imagepickerkmp.scanner.utils.LoggerFactory
import io.github.ismoy.imagepickerkmp.scanner.utils.scanner
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionMixWithOthers
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

internal class IOSScannerSoundManager(
    private val config: ScannerCameraConfig
) {
    private var audioPlayer: AVAudioPlayer? = null
    private val logger = LoggerFactory.getLogger()

    private val wantsSound: Boolean
        get() = config.behavior.playSound ||
            config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_AND_VIBRATE ||
            config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_ONLY

    init {
        ensureAudioPlayer()
    }

    private fun ensureAudioPlayer() {
        if (wantsSound && audioPlayer == null) setupAudioPlayer()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupAudioPlayer() {
        try {
            logger.scanner("Setting up audio player...")

            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(
                AVAudioSessionCategoryPlayback,
                withOptions = AVAudioSessionCategoryOptionMixWithOthers,
                error = null
            )
            audioSession.setActive(true, error = null)
            logger.scanner("Audio session configured successfully")

            val resourcePath = NSBundle.mainBundle.pathForResource(config.behavior.soundResourceName, config.behavior.soundResourceExtension)
            if (resourcePath == null) {
                logger.error("Audio", "Error: Could not find ${config.behavior.soundResourceName}.${config.behavior.soundResourceExtension} in bundle")
                return
            }
            logger.scanner("Found sound file at: $resourcePath")

            val soundUrl = NSURL.fileURLWithPath(resourcePath)
            logger.scanner("URL of the created file: ${soundUrl.absoluteString}")

            audioPlayer = AVAudioPlayer(contentsOfURL = soundUrl, error = null)

            audioPlayer?.let { player ->
                if (player.prepareToPlay()) {
                    logger.scanner("Player prepared correctly")
                    player.volume = 1.0f
                    player.numberOfLoops = 0

                    if (player.play()) {
                        logger.scanner("Sound check successful")
                        player.stop()
                        player.currentTime = 0.0
                    } else {
                        logger.error("Audio", "Error: Sound could not be played in the test")
                    }
                } else {
                    logger.error("Audio", "Error: Failed to prepare player")
                }
            } ?: logger.error("Audio", "Error: Failed to create audio player")

        } catch (e: Exception) {
            logger.error("Audio", "Error in AudioPlayer setup: ${e.message}", e)
        }
    }

    fun playBeepSound() {
        if (wantsSound) {
            ensureAudioPlayer()
            try {
                audioPlayer?.let { player ->
                    player.currentTime = 0.0
                    player.play()
                } ?: AudioServicesPlaySystemSound(1052u)
            } catch (exception: Exception) {
                logger.error("Audio", "Error playing sound: ${exception.message}", exception)
            }
        }

        val wantsVibrate = config.behavior.hapticFeedback == HapticFeedbackMode.SOUND_AND_VIBRATE ||
                           config.behavior.hapticFeedback == HapticFeedbackMode.VIBRATE_ONLY

        if (wantsVibrate) {
            try {
                UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy).impactOccurred()
            } catch (_: Exception) {
                AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
        }
    }

    fun release() {
        audioPlayer?.stop()
        audioPlayer = null
    }
}
