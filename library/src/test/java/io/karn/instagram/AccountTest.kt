package io.karn.instagram

import io.karn.instagram.core.SyntheticResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class AccountTest : TestBase() {

    @Test
    fun validateSignedInUserInformation() {
        val res = Instagram.getInstance().account.getAccount(Instagram.session.primaryKey)

        assertTrue(res is SyntheticResponse.AccountDetails.Success)
    }

    @Test
    fun validateSingedInUserFeed() {
        val res = Instagram.getInstance().account.getFeed(Instagram.session.primaryKey)

        System.out.println(res)

        assertTrue(res is SyntheticResponse.ProfileFeed.Success)
        assertEquals(0, res.feed.length())
    }

    @Test
    fun validateFollowers() {
        val res = Instagram.getInstance().account.getFollowers(Instagram.session.primaryKey)

        System.out.println(res)

        assertTrue(res is SyntheticResponse.Relationships.Success)
        assertEquals(0, res.relationships.length())
    }

    @Test
    fun validateFollowing() {
        val res = Instagram.getInstance().account.getFollowing(Instagram.session.primaryKey)

        System.out.println(res)

        assertTrue(res is SyntheticResponse.Relationships.Success)
        assertEquals(0, res.relationships.length())
    }
}
