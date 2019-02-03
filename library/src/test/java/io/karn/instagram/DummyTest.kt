package io.karn.instagram

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DummyTest : TestBase() {

    lateinit var username: String
    lateinit var password: String

    @Before
    fun beforeTest() {
        username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
        password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")
    }

    @Test
    fun testNetwork() {
        Instagram.init()

        val res = Instagram.getInstance().authentication.authenticate(username, password)

        System.out.println(res)
    }
}
