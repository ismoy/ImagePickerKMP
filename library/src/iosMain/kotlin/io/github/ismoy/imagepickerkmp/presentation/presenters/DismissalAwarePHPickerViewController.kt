package io.github.ismoy.imagepickerkmp.presentation.presenters

import io.github.ismoy.imagepickerkmp.data.delegates.PHPickerDelegate
import io.ktor.util.date.getTimeMillis
import kotlinx.cinterop.ExperimentalForeignApi
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalForeignApi::class)
object DismissalAwarePHPickerViewController {
    private val dismissalMonitors = mutableMapOf<Int, DismissalMonitor>()
    
    fun createPickerViewController(
        configuration: PHPickerConfiguration,
        pickerDelegate: PHPickerDelegate
    ): PHPickerViewController {
        val picker = PHPickerViewController(configuration)
        
        val monitor = DismissalMonitor(picker, pickerDelegate)
        dismissalMonitors[picker.hashCode()] = monitor
        monitor.startMonitoring()
        
        return picker
    }
    
    fun markDismissalHandled(picker: PHPickerViewController) {
        val hashCode = picker.hashCode()
        dismissalMonitors[hashCode]?.stopMonitoring()
        dismissalMonitors.remove(hashCode)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class DismissalMonitor(
    private val picker: PHPickerViewController,
    private val pickerDelegate: PHPickerDelegate
) {
    private var isMonitoring = false
    private var monitoringStartTime = 0L
    
    @OptIn(ExperimentalTime::class)
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        monitoringStartTime = getTimeMillis()
        scheduleCheck()
    }
    
    fun stopMonitoring() {
        isMonitoring = false
    }
    
    private fun scheduleCheck() {
        if (!isMonitoring) return
        
        dispatch_async(dispatch_get_main_queue()) {
            if (isMonitoring && picker.viewIfLoaded?.window == null) {
                stopMonitoring()
                pickerDelegate.onPickerDismissed()
                DismissalAwarePHPickerViewController.markDismissalHandled(picker)
            } else if (isMonitoring) {
                dispatch_async(dispatch_get_main_queue()) {
                    scheduleCheck()
                }
            }
        }
    }
}
