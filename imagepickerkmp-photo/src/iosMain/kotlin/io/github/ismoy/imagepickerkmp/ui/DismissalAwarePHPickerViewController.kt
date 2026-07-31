package io.github.ismoy.imagepickerkmp.ui

import io.github.ismoy.imagepickerkmp.gallery.PHPickerDelegate
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerViewController

@OptIn(ExperimentalForeignApi::class)
internal object DismissalAwarePHPickerViewController {
    
    // Strong reference prevents the monitor (and transitively the delegate) from being GC'd
    // while the picker is presented. Cleared once dismissal is detected.
    private var activeMonitor: DismissalMonitor? = null
    
    fun createPickerViewController(
        configuration: PHPickerConfiguration,
        pickerDelegate: PHPickerDelegate
    ): PHPickerViewController {
        val picker = PHPickerViewController(configuration)
        activeMonitor = DismissalMonitor(picker, pickerDelegate) {
            activeMonitor = null
        }
        return picker
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DismissalMonitor(
    private val picker: PHPickerViewController,
    private val pickerDelegate: PHPickerDelegate,
    private val onCleanup: () -> Unit
) {
    
    private var didNotifyDismissal = false
    // Start as false — the picker hasn't appeared in a window yet during presentation animation
    private var hasAppearedInWindow = false
    
    init {
        scheduleCheck()
    }
    
    private fun scheduleCheck() {
        platform.darwin.dispatch_after(
            platform.darwin.dispatch_time(
                platform.darwin.DISPATCH_TIME_NOW,
                500_000_000L
            ),
            platform.darwin.dispatch_get_main_queue()
        ) {
            performCheck()
        }
    }
    
    private fun performCheck() {
        if (didNotifyDismissal) return
        
        val isInWindow = picker.view.window != null
        
        // Track when the picker first appears in a window (presentation animation completed)
        if (isInWindow) {
            hasAppearedInWindow = true
        }
        
        // Only consider it a dismissal if it was previously visible and is now gone
        if (hasAppearedInWindow && !isInWindow) {
            didNotifyDismissal = true
            if (!pickerDelegate.dismissHandled) {
                pickerDelegate.onPickerDismissed()
            }
            onCleanup()
            return
        }
        
        scheduleCheck()
    }
}
