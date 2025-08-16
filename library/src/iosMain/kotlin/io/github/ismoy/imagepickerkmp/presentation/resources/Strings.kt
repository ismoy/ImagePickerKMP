package io.github.ismoy.imagepickerkmp.presentation.resources

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

@OptIn(ExperimentalForeignApi::class)
private val defaultStringsMap: Map<String, String> by lazy {
        val bundle = NSBundle.mainBundle
    val path = bundle.pathForResource("strings.default", "properties") ?: return@lazy emptyMap()
    val content = NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: return@lazy emptyMap()

    content
        .lineSequence()
        .filter { it.isNotBlank() && !it.trim().startsWith("#") }
        .mapNotNull { line ->
            val (key, value) = line.split("=", limit = 2).map { it.trim() }
            if (key.isNotEmpty() && value.isNotEmpty()) key to value else null
        }.toMap()
}

@Composable
internal actual fun stringResource(id: StringResource): String = getDefaultString(id)

private fun getDefaultString(id: StringResource): String {
    val key = when (id) {
        StringResource.GALLERY_PERMISSION_REQUIRED -> "gallery_permission_required"
        StringResource.GALLERY_PERMISSION_DESCRIPTION -> "gallery_permission_description"
        StringResource.GALLERY_PERMISSION_DENIED -> "gallery_permission_denied"
        StringResource.GALLERY_PERMISSION_DENIED_DESCRIPTION -> "gallery_permission_denied_description"
        StringResource.GALLERY_GRANT_PERMISSION -> "grant_gallery_permission"
        StringResource.GALLERY_BTN_SETTINGS -> "gallery_btn_settings"
        else -> id.name.lowercase()
    }
    return defaultStringsMap[key] ?: id.name.replace("_", " ").replaceFirstChar { it.uppercase() }
}
