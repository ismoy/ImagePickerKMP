package io.github.ismoy.imagepickerkmp.core

import android.app.Application

internal object CoreServicesHolder {
    private var application: Application? = null

    fun init(app: Application) {
        if (application == null) application = app
    }

    fun requireApplication(): Application = requireNotNull(application) {
        "CoreInitializer did not run. Make sure the imagepicker-core library is on the classpath."
    }
}
