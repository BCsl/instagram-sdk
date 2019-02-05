package io.karn.instagram

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class InitializationTest : TestBase() {

    @Before
    fun beforeTest() {
        // Initialize the library
        Instagram.init(applicationContext) {
            requestLogger = { method, url, statusCode, userAgent -> }
        }
    }

    @Test
    fun startUp_validateInitialization() {
        assertNotNull(Instagram.getInstance().authentication)
        assertNotNull(Instagram.getInstance().account)
        assertNotNull(Instagram.getInstance().search)
        assertNotNull(Instagram.getInstance().stories)

        // Validate configuration
        assertNotNull(Instagram.config.requestLogger)
    }
}
