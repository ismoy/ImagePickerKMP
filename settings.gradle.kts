pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "ImagePickerKMP"
include(":imagepicker-core")
include(":imagepickerkmp-photo")
include(":imagepickerkmp-video")
include(":imagepickerkmp-audio")
include(":imagepickerkmp-audio-player")
include(":imagepickerkmp-scanner")
include(":imagepickerkmp-video-player")
