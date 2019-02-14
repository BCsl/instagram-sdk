package io.karn.instagram

import android.content.Context
import io.karn.instagram.core.Configuration
import io.karn.instagram.core.Session
import io.karn.instagram.endpoints.*
import khttp.KHttpConfig

/**
 * The 'Instagram' class is the primary entry point for SDK related functions. Be sure to execute the
 * {@link #init(Configuration)} function to initialize the library with default or custom configuration.
 *
 * Note the the SDK itself is synchronous and allows the developer the flexibility to implement their
 * preferred async pattern.
 */
class Instagram private constructor(private val configuration: Configuration) {

    companion object {
        private val NOT_INITIALIZED_ERROR = IllegalStateException("Call `Instagram.init(...)` before calling this method.")

        internal var instance: Instagram? = null

        /**
         * Initialize the Instagram SDK with the provided configuration. This function must be executed before other
         * parts of the library are interacted with.
         */
        fun init(context: Context, configure: (Configuration.() -> Unit) = {}) {
            if (instance != null) return

            // Initialize the Configuration.
            val displayMetrics = context.resources.displayMetrics
            val config = Configuration(
                    deviceDPI = "${displayMetrics.densityDpi}dpi",
                    deviceResolution = "${displayMetrics.widthPixels}x${displayMetrics.heightPixels}"
            )

            // Apply any changes.
            config.configure()

            // Build instance.
            instance = Instagram(config)
        }

        fun getInstance(): Instagram {
            return instance ?: throw NOT_INITIALIZED_ERROR
        }

        var session: Session
            get() = instance?._session ?: throw NOT_INITIALIZED_ERROR
            set(value) {
                instance?._session = value
            }

        internal val config: Configuration
            // Return a copy to prevent accidental mutation.
            get() = instance?.configuration ?: throw NOT_INITIALIZED_ERROR
    }

    private var _session: Session = Session()
    val authentication: Authentication = Authentication()
    val account: Account = Account()
    val search: Search = Search()
    val stories: Stories = Stories()
    val media: Media = Media()

    init {
        // Log network calls if needed.
        // TODO: Investigate whether or not we need buffered writers instead.
        configuration.requestLogger?.let { logger ->
            KHttpConfig.attachInterceptor {
                logger.invoke(
                        it.request.method,
                        it.request.url,
                        it.statusCode,
                        it.request.headers["User-Agent"] ?: ""
                )
            }
        }
    }
}
