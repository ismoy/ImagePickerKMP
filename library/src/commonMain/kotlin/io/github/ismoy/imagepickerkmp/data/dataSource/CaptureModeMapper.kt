
package io.github.ismoy.imagepickerkmp.data.dataSource

import io.github.ismoy.imagepickerkmp.domain.models.CapturePhotoPreference

internal expect fun getCaptureMode(preference: CapturePhotoPreference): Int
