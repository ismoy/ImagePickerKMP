package io.github.ismoy.imagepickerkmp.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.darwin.NSObject

@Composable
fun AppLifecycleObserver(
    onAppBecomeActive: (() -> Unit)? = null,
    onAppResignActive: (() -> Unit)? = null
) {
    val observer = remember { AppLifecycleObserverImpl() }
    
    DisposableEffect(Unit) {
        observer.startObserving(onAppBecomeActive, onAppResignActive)
        
        onDispose {
            observer.stopObserving()
        }
    }
}

private class AppLifecycleObserverImpl : NSObject() {
    private var didBecomeActiveObserver: Any? = null
    private var willResignActiveObserver: Any? = null
    
    fun startObserving(
        onAppBecomeActive: (() -> Unit)?,
        onAppResignActive: (() -> Unit)?
    ) {
        val notificationCenter = NSNotificationCenter.defaultCenter
        
        didBecomeActiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onAppBecomeActive?.invoke()
        }
        
        willResignActiveObserver = notificationCenter.addObserverForName(
            name = UIApplicationWillResignActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->
            onAppResignActive?.invoke()
        }
    }
    
    fun stopObserving() {
        val notificationCenter = NSNotificationCenter.defaultCenter
        
        didBecomeActiveObserver?.let { observer ->
            notificationCenter.removeObserver(observer)
        }
        
        willResignActiveObserver?.let { observer ->
            notificationCenter.removeObserver(observer)
        }
        
        didBecomeActiveObserver = null
        willResignActiveObserver = null
    }
}
