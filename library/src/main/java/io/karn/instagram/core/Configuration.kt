package io.karn.instagram.core


/**
 * Configuration data class for the library, modify the attributes when initializing the library to change its behaviour
 * and or manage the defaults.
 */
data class Configuration(
        /**
         * Specify the device DPI to ensure that the API serves the correct asset dimensions.
         */
        internal val deviceDPI: String = Crypto.DPI,
        /**
         * Specify the device resolution to ensure that the API serves the correct asset dimensions.
         */
        internal val deviceResolution: String = Crypto.DISPLAY_RESOLUTION,
        /**
         * Attach a logger to process API calls.
         *
         * @param requestMethod The standard HTTP request method -- e.g GET.
         * @param url           The URL associated with the HTTP request.
         * @param statusCode    The resulting status code -- e.g 200
         * @param userAgent     The user-agent provided for the HTTP request.
         */
        var requestLogger: ((requestMethod: String, url: String, statusCode: Int, userAgent: String) -> Unit)? = null
)
