package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.PHPickerDelegate
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerViewController

@OptIn(ExperimentalForeignApi::class)
object DismissalAwarePHPickerViewController {
    
    private val pickerMonitors = mutableMapOf<Int, DismissalMonitor>()
    
    fun createPickerViewController(
        configuration: PHPickerConfiguration,
        pickerDelegate: PHPickerDelegate
    ): PHPickerViewController {
        val picker = PHPickerViewController(configuration)
        
        val monitor = DismissalMonitor(picker, pickerDelegate)
        pickerMonitors[picker.hashCode()] = monitor
        
        return picker
    }
    
    fun markDismissalHandled(picker: PHPickerViewController) {
        pickerMonitors.remove(picker.hashCode())
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DismissalMonitor(
    private val picker: PHPickerViewController,
    private val pickerDelegate: PHPickerDelegate
) {
    
    private var didNotifyDismissal = false
    private var previousWindowState: Boolean = true
    
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
        val currentWindowState = picker.view?.window != null
        
        if (previousWindowState && !currentWindowState && !didNotifyDismissal) {
            didNotifyDismissal = true
            if (!pickerDelegate.dismissHandled) {
                pickerDelegate.onPickerDismissed()
            }
            return
        }
        
        previousWindowState = currentWindowState
        
        if (!didNotifyDismissal) {
            scheduleCheck()
        }
    }
}
