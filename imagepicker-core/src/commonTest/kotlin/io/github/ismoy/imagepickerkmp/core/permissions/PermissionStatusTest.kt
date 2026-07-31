package io.github.ismoy.imagepickerkmp.core.permissions

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PermissionStatusTest : DescribeSpec({

    describe("PermissionStatus singletons") {
        it("Granted is same instance across references") {
            (PermissionStatus.Granted === PermissionStatus.Granted) shouldBe true
        }
        it("Denied is same instance across references") {
            (PermissionStatus.Denied === PermissionStatus.Denied) shouldBe true
        }
        it("DeniedPermanently is same instance across references") {
            (PermissionStatus.DeniedPermanently === PermissionStatus.DeniedPermanently) shouldBe true
        }
    }

    describe("PermissionStatus equality") {
        it("Granted equals Granted") {
            PermissionStatus.Granted shouldBe PermissionStatus.Granted
        }
        it("Granted does not equal Denied") {
            PermissionStatus.Granted shouldNotBe PermissionStatus.Denied
        }
        it("Denied does not equal DeniedPermanently") {
            PermissionStatus.Denied shouldNotBe PermissionStatus.DeniedPermanently
        }
    }

    describe("PermissionStatus type checks") {
        it("Granted is PermissionStatus") {
            PermissionStatus.Granted.shouldBeInstanceOf<PermissionStatus>()
        }
        it("Denied is PermissionStatus") {
            PermissionStatus.Denied.shouldBeInstanceOf<PermissionStatus>()
        }
        it("DeniedPermanently is PermissionStatus") {
            PermissionStatus.DeniedPermanently.shouldBeInstanceOf<PermissionStatus>()
        }
    }

    describe("PermissionStatus sealed when expression") {
        it("exhaustive when covers all 3 variants") {
            val statuses: List<PermissionStatus> = listOf(
                PermissionStatus.Granted,
                PermissionStatus.Denied,
                PermissionStatus.DeniedPermanently
            )
            val labels = statuses.map { s ->
                when (s) {
                    is PermissionStatus.Granted -> "granted"
                    is PermissionStatus.Denied -> "denied"
                    is PermissionStatus.DeniedPermanently -> "permanently_denied"
                }
            }
            labels shouldBe listOf("granted", "denied", "permanently_denied")
        }
    }

    describe("PermissionType sealed interface") {
        it("Camera is PermissionType") {
            PermissionType.Camera.shouldBeInstanceOf<PermissionType>()
        }
        it("Gallery is PermissionType") {
            PermissionType.Gallery.shouldBeInstanceOf<PermissionType>()
        }
        it("Storage is PermissionType") {
            PermissionType.Storage.shouldBeInstanceOf<PermissionType>()
        }
        it("Microphone is PermissionType") {
            PermissionType.Microphone.shouldBeInstanceOf<PermissionType>()
        }
        it("Camera is not equal to Gallery") {
            (PermissionType.Camera == PermissionType.Gallery) shouldBe false
        }
        it("Camera is same instance as itself") {
            (PermissionType.Camera === PermissionType.Camera) shouldBe true
        }
        it("exhaustive when covers all 4 types") {
            val types: List<PermissionType> = listOf(
                PermissionType.Camera,
                PermissionType.Gallery,
                PermissionType.Storage,
                PermissionType.Microphone
            )
            val labels = types.map { t ->
                when (t) {
                    is PermissionType.Camera -> "camera"
                    is PermissionType.Gallery -> "gallery"
                    is PermissionType.Storage -> "storage"
                    is PermissionType.Microphone -> "microphone"
                }
            }
            labels shouldBe listOf("camera", "gallery", "storage", "microphone")
        }
    }
})
