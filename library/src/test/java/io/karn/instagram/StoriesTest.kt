package io.karn.instagram

import io.karn.instagram.core.SyntheticResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class StoriesTest : TestBase() {

    @Test
    fun validateStories() {
        val res = Instagram.getInstance().stories.getStories(Instagram.session.primaryKey)

        assertTrue(res is SyntheticResponse.StoryReel.Success)
    }
}
