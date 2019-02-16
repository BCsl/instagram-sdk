package io.karn.instagram

import android.app.Application
import io.karn.instagram.core.SyntheticResponse
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

open class TestBase {
    companion object {
        val applicationContext: Application = RuntimeEnvironment.application

        var intialized = false
    }

    init {
        if (!intialized) {
            intialized = true

            System.out.println("Initializing")

            // Initialize the library
            Instagram.init(RuntimeEnvironment.application) {
                requestLogger = { method, url, statusCode, userAgent -> }
            }

            val username = System.getenv("DEFAULT_USERNAME") ?: throw IllegalStateException("No username specified.")
            val password = System.getenv("DEFAULT_PASSWORD") ?: throw IllegalStateException("No password specified.")

            // Authenticate the user.
            val res = Instagram.getInstance().authentication.authenticate(username, password)

            System.out.println(res)

            assertTrue(res is SyntheticResponse.Auth.Success)

            assertNotNull(res.data.optJSONObject("logged_in_user"))
            assertNotNull(res.data.optJSONArray("cookie"))
            assertNotEquals("", res.data.optString("uuid"))

            assertNotNull(res)

            System.out.println("Done initializing.")
        }
    }
}
