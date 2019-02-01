package core

data class Configuration(
        val deviceUA: Boolean = false,
        val deviceDPI: String = Crypto.DPI,
        val deviceResolution: String = Crypto.DISPLAY_RESOLUTION,
        val requestLogger: ((String, String, Int) -> Unit)? = null,
        internal val maxRelationshipFetch: Int = 10000
) {
    init {
        deviceDPI.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid DPI value -- e.g '320dpi'.")

        deviceResolution.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid resolution value -- e.g '1080x1920'.")
    }
}