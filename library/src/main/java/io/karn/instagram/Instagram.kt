package io.karn.instagram

import io.karn.instagram.core.Configuration
import io.karn.instagram.core.Session
import io.karn.instagram.endpoints.Account
import io.karn.instagram.endpoints.Authentication
import io.karn.instagram.endpoints.Search
import io.karn.instagram.endpoints.Stories
import khttp.KHttpConfig

/**
 * The 'Instagram' class is the primary entry point for SDK related functions. Be sure to execute the
 * {@link #init(Configuration)} function to initialize the library with default or custom configuration.
 *
 * Note the the SDK itself is synchronous and allows the developer the flexibility to implement their
 * preferred async pattern.
 */
class Instagram private constructor(val configuration: Configuration) {

    companion object {
        private val NOT_INITIALIZED_ERROR = IllegalStateException("Call `Instagram.init(...)` before calling this method.")

        private var instance: Instagram? = null

        /**
         * Initialize the Instagram SDK with the provided configuration. This function must be executed before other
         * parts of the library are interacted with.
         */
        fun init(configuration: Configuration = Configuration()) {
            if (instance != null) return

            instance = Instagram(configuration)
        }

        fun getInstance(): Instagram {
            return instance ?: throw NOT_INITIALIZED_ERROR
        }

        var session: Session
            get() {
                return instance?._session ?: throw NOT_INITIALIZED_ERROR
            }
            set(value) {
                instance?._session = value
            }
    }

    private var _session: Session = Session()
    val authentication: Authentication = Authentication()
    val account: Account = Account()
    val search: Search = Search()
    val stories: Stories = Stories()

    init {
        // Log network calls if needed.
        // TODO: Investigate whether or not we need buffered writers instead.
        configuration.requestLogger?.let { logger ->
            KHttpConfig.attachInterceptor {
                logger.invoke(it.request.method, it.request.url, it.statusCode)
            }
        }
    }
}
