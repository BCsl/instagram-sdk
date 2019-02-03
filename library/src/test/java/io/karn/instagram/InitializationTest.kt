package io.karn.instagram

import org.junit.Before
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
        // username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
        // password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")

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
}
