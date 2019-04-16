package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.StoriesAPI
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.exceptions.InstagramAPIException
import org.json.JSONArray

class Stories internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a story reel API request.
     *
     * @param userKey    The User Key associated with the profile.
     * @return  A {@link SyntheticResponse.StoryReel} object.
     */
    fun getStories(userKey: String): SyntheticResponse.StoryReel {
        val (res, error) = wrapAPIException { StoriesAPI.getStories(userKey, Instagram.session) }

        res ?: return SyntheticResponse.StoryReel.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                val reel = res.jsonObject.optJSONObject("reel")
                        ?.optJSONArray("items")
                        ?: JSONArray()

                SyntheticResponse.StoryReel.Success(reel)
            }
            else -> SyntheticResponse.StoryReel.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }
}
