package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.DirectMessagesAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.exceptions.InstagramAPIException
import org.json.JSONArray

class DirectMessages internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a direct messages lookup API request.
     *
     * @param maxId The pagination ID to complete the API request.
     * @return  A {@link SyntheticResponse.DirectMessages} object.
     */
    fun get(maxId: String = ""): SyntheticResponse.DirectMessages {
        val (res, error) = wrapAPIException { DirectMessagesAPI.getMessages(maxId, Instagram.session) }

        res ?: return SyntheticResponse.DirectMessages.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                val threads = res.jsonObject.optJSONObject("inbox")?.optJSONArray("threads") ?: JSONArray()
                val unseenCount = res.jsonObject.optJSONObject("inbox")?.optInt("unseen_count", 0) ?: 0

                if (threads.length() == 0) {
                    SyntheticResponse.DirectMessages.Failure(InstagramAPIException(res.statusCode, Errors.ERROR_SEARCH_NO_RESULTS))
                } else {
                    SyntheticResponse.DirectMessages.Success(unseenCount, threads)
                }
            }
            else -> SyntheticResponse.DirectMessages.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }
}
