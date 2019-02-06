package io.karn.instagram

import io.karn.instagram.core.SyntheticResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AccountTest : TestBase() {

    lateinit var username: String
    lateinit var password: String

    init {
        // Initialize the library
        Instagram.init(applicationContext) {
            requestLogger = { method, url, statusCode, userAgent -> }
        }
    }

    @Before
    fun before() {
        username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
        password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")

        // Authenticate the user.
        val res = Instagram.getInstance().authentication.authenticate(username, password)

        assertTrue(res is SyntheticResponse.Auth.Success)

        assertNotNull(res.data.optJSONObject("logged_in_user"))
        assertNotNull(res.data.optJSONArray("cookie"))
        assertNotEquals("", res.data.optString("uuid"))

        assertNotNull(res)
    }

    @Test
    fun account_validateSignedInUserInformation() {
        val res = Instagram.getInstance().account.getAccount(Instagram.session.primaryKey)

        assertTrue(res is SyntheticResponse.AccountDetails.Success)
    }

    @Test
    fun account_validateSingedInUserFeed() {
        val res = Instagram.getInstance().account.getFeed(Instagram.session.primaryKey)

        System.out.println(res)

        assertTrue(res is SyntheticResponse.ProfileFeed.Success)
        assertEquals(0, res.feed.length())
    }
}