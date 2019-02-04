package io.karn.instagram.core

import io.karn.instagram.common.Experimental


/**
 * Configuration data class for the library, modify the attributes when initializing the library to change its behaviour
 * and or manage the defaults.
 */
data class Configuration(
        /**
         * Flag to use the UserAgent of the device when making API requests. This functionality is currently untested,
         * use `false` when in production.
         */
        @Experimental
        val deviceUA: Boolean = false,
        internal val deviceDPI: String = Crypto.DPI,
        internal val deviceResolution: String = Crypto.DISPLAY_RESOLUTION,
        /**
         * Attach a logger to process API calls.
         */
        val requestLogger: ((String, String, Int) -> Unit)? = null
) {
    init {
        deviceDPI.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid DPI value -- e.g '320dpi'.")

        deviceResolution.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid resolution value -- e.g '1080x1920'.")
    }
}