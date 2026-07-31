package io.github.ismoy.imagepickerkmp.core

import android.app.Application
import io.github.ismoy.imagepickerkmp.core.language.getLanguageDevice

internal object CoreServicesHolder {
    private var application: Application? = null

    fun init(app: Application) {
        if (application == null) application = app
        val localeLanguage = getLanguageDevice()
        I18nKonfig.setLocale(localeLanguage)
    }

    fun requireApplication(): Application = requireNotNull(application) {
        "CoreInitializer did not run. Make sure the imagepicker-core library is on the classpath."
    }
}
