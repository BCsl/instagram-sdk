package io.karn.instagram

import android.app.Application
import io.karn.instagram.core.SyntheticResponse
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

open class SignedInTestBase {

    val applicationContext: Application = RuntimeEnvironment.application

    var username: String
    var password: String

    init {
        // Initialize the library
        Instagram.init(RuntimeEnvironment.application) {
            requestLogger = { method, url, statusCode, userAgent -> }
        }

        username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
        password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")

        // Authenticate the user.
        val res = Instagram.getInstance().authentication.authenticate(username, password)

        System.out.println(res)

        assertTrue(res is SyntheticResponse.Auth.Success)

        assertNotNull(res.data.optJSONObject("logged_in_user"))
        assertNotNull(res.data.optJSONArray("cookie"))
        assertNotEquals("", res.data.optString("uuid"))

        assertNotNull(res)
    }
}
