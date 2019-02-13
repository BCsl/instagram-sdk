package io.karn.instagram

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertFails
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class InitializationTest {

    @Test
    fun startUp_validateInitialization() {
        Instagram.instance = null

        assertFails {
            Instagram.getInstance()
        }

        assertFails {
            Instagram.session
        }

        assertFails {
            Instagram.config
        }

        // Initialize the library
        Instagram.init(RuntimeEnvironment.application) {
            requestLogger = { method, url, statusCode, userAgent -> }
        }

        assertNotNull(Instagram.getInstance().authentication)
        assertNotNull(Instagram.getInstance().account)
        assertNotNull(Instagram.getInstance().search)
        assertNotNull(Instagram.getInstance().stories)

        // Validate configuration
        assertNotNull(Instagram.config.requestLogger)
    }
}
