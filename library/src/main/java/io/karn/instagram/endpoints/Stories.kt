package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.StoriesAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.core.SyntheticResponse
import org.json.JSONArray

class Stories internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a story reel API request.
     *
     * @param userKey    The User Key associated with the profile.
     * @return  A {@link SyntheticResponse.StoryReel} object.
     */
    fun getStories(userKey: String): SyntheticResponse.StoryReel =
            StoriesAPI.getStories(userKey, Instagram.session)
                    .let {
                        return@let when (it.statusCode) {
                            200 -> {
                                val reel = it.jsonObject.optJSONObject("reel")
                                        ?.optJSONArray("items")
                                        ?: JSONArray()

                                SyntheticResponse.StoryReel.Success(reel)
                            }
                            else -> SyntheticResponse.StoryReel.Failure(String.format(Errors.ERROR_STORIES_FAILED, it.statusCode, it.text))
                        }
                    }
}
