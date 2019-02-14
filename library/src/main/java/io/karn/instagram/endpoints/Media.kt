package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.MediaAPI
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.SyntheticResponse
import org.json.JSONArray

class Media internal constructor() {
    /**
     * Create a SyntheticResponse from the response of a media likes API request.
     *
     * @param mediaKey    The Media Key associated with the post.
     * @return  A {@link SyntheticResponse.MediaLikess} object.
     */
    fun getLikes(mediaKey: String): SyntheticResponse.MediaLikes {
        val (res, error) = wrapAPIException { MediaAPI.getLikes(mediaKey, Instagram.session) }

        res ?: return SyntheticResponse.MediaLikes.Failure(error!!.statusCode, error.statusMessage)

        // Handle error messages.
        return when (res.statusCode) {
            200 -> SyntheticResponse.MediaLikes.Success(res.jsonObject.optJSONArray("users") ?: JSONArray())
            else -> SyntheticResponse.MediaLikes.Failure(res.statusCode, res.text)
        }
    }

    /**
     * Create a SyntheticResponse from the response of a media comments API request.
     *
     * @param mediaKey    The Media Key associated with the post.
     * @return  A {@link SyntheticResponse.MediaLikess} object.
     */
    fun getComments(mediaKey: String): SyntheticResponse.MediaComments {
        val (res, error) = wrapAPIException { MediaAPI.getComments(mediaKey, Instagram.session) }

        res ?: return SyntheticResponse.MediaComments.Failure(error!!.statusCode, error.statusMessage)

        // Handle error messages.
        return when (res.statusCode) {
            200 -> SyntheticResponse.MediaComments.Success(res.jsonObject.optJSONArray("comments") ?: JSONArray())
            else -> SyntheticResponse.MediaComments.Failure(res.statusCode, res.text)
        }
    }
}