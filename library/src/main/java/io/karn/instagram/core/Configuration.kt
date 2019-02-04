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
        var deviceUA: Boolean = false,
        @Experimental
        var deviceDPI: String = Crypto.DPI,
        @Experimental
        var deviceResolution: String = Crypto.DISPLAY_RESOLUTION,
        /**
         * Attach a logger to process API calls.
         *
         * @param requestMethod The standard HTTP request method -- e.g GET.
         * @param url           The URL associated with the HTTP request.
         * @param statusCode    The resulting status code -- e.g 200
         * @param userAgent     The user-agent provided for the HTTP request.
         */
        var requestLogger: ((requestMethod: String, url: String, statusCode: Int, userAgent: String) -> Unit)? = null
) {
    init {
        deviceDPI.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid DPI value -- e.g '320dpi'.")

        deviceResolution.takeIf { !it.isBlank() }
                ?: throw IllegalArgumentException("Must specify a valid resolution value -- e.g '1080x1920'.")
    }

    companion object {
        fun getDefaultUserAgent(): String {
            return Crypto.USER_AGENT
        }

        fun getUserAgent(): String {
            return Crypto.buildUserAgent()
        }
    }
}