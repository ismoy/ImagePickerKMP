package io.github.ismoy.imagepickerkmp.scanner.camera.config

data class EnterpriseOverlayConfig(
    val title: String? = null,
    val tag: String? = null,
    val statusLabel: String? = null,
    val statusValue: String? = null,
    val infoLine1Label: String? = null,
    val infoLine1Value: String? = null,
    val infoLine2Label: String? = null,
    val infoLine2Value: String? = null,
    val footerLeftLines: List<String> = emptyList(),
    val footerRightLines: List<String> = emptyList(),
    val showIdleStats: Boolean = false
)
