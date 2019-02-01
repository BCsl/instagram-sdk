package endpoints

import Instagram
import common.Errors
import core.Crypto
import core.Endpoints
import core.SyntheticResponse
import khttp.get
import org.json.JSONArray

class Stories {

    companion object {
        private const val MEMBER_REEL = "reel"
        private const val MEMBER_REEL_ITEMS = "items"
    }

    fun getStories(primaryKey: String): SyntheticResponse.StoryReelResult = get(url = String.format(Endpoints.STORIES, primaryKey),
            headers = Crypto.HEADERS,
            cookies = Instagram.getDefaultInstance().session.cookieJar)
            .let {
                return@let when (it.statusCode) {
                    200 -> {
                        val reel = it.jsonObject.optJSONObject(MEMBER_REEL)
                                ?.optJSONArray(MEMBER_REEL_ITEMS)
                                ?: JSONArray()

                        SyntheticResponse.StoryReelResult.Success(reel)
                    }
                    else -> SyntheticResponse.StoryReelResult.Failure(String.format(Errors.ERROR_STORIES_FAILED, it.statusCode, it.text))
                }
            }
}
