import core.Configuration
import core.Session
import endpoints.Account
import endpoints.Authentication
import endpoints.Search
import endpoints.Stories
import khttp.KHttpConfig

class Instagram private constructor(val configuration: Configuration) {

    companion object {

        private var instance: Instagram? = null

        /**
         * Initialize with configurations and feature toggles.
         */
        fun init(configuration: Configuration = Configuration()) {
            if (instance != null) return

            instance = Instagram(configuration)
        }

        fun getDefaultInstance(): Instagram {
            if (instance == null) {
                throw IllegalStateException("Call `Instagram.init(...)` before calling this method.")
            }

            return instance as Instagram
        }
    }

    val session: Session = Session()
    val authentication: Authentication = Authentication()
    val account: Account = Account()
    val search: Search = Search()
    val stories: Stories = Stories()

    init {
        configuration.requestLogger?.let { logger ->

            KHttpConfig.attachInterceptor {
                logger.invoke(it.request.method, it.request.url, it.statusCode)
            }
        }
    }
}
