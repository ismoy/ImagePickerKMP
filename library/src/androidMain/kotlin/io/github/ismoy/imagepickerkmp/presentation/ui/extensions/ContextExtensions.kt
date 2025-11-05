package io.github.ismoy.imagepickerkmp.presentation.ui.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

val Context.activity: Activity?
    get() = when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.activity
        else -> null
    }