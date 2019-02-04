package io.karn.instagram

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class InitializationTest : TestBase() {

    lateinit var username: String
    lateinit var password: String

    @Before
    fun beforeTest() {
        // Initialize the library
        Instagram.init {
            deviceUA = true
        }
    }

    @Test
    fun startUp_validateInitialization() {
        assertNotNull(Instagram.getInstance().authentication)
        assertNotNull(Instagram.getInstance().account)
        assertNotNull(Instagram.getInstance().search)
        assertNotNull(Instagram.getInstance().stories)

        assertTrue(Instagram.config.deviceUA)

        Instagram.config.deviceUA = false

        assertTrue(Instagram.config.deviceUA)
        assertNull(Instagram.config.requestLogger)
    }

    @Test
    @Ignore
    fun startUp_testAuthentication() {
        username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
        password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")

        val res = Instagram.getInstance().authentication.authenticate(username, password)

        System.out.println(res)

        assertNotNull(res)
    }
}
