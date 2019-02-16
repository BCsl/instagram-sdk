package io.karn.instagram

import io.karn.instagram.core.SyntheticResponse
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Ignore
class DirectMessagesTest : TestBase() {

    @Test
    fun directMessages() {
        val res = Instagram.getInstance().directMessages.get()

        System.out.println(res)

        assertTrue(res is SyntheticResponse.DirectMessages.Success)
    }
}
