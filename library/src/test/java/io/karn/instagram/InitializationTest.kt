package io.karn.instagram

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class InitializationTest : TestBase() {

    lateinit var username: String
    lateinit var password: String

    @Before
    fun beforeTest() {
        // Initialize the library
        Instagram.init()
    }

    @Test
    fun startUp_validateInitialization() {
        assertNotNull(Instagram.getInstance().authentication)
        assertNotNull(Instagram.getInstance().account)
        assertNotNull(Instagram.getInstance().search)
        assertNotNull(Instagram.getInstance().stories)
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
