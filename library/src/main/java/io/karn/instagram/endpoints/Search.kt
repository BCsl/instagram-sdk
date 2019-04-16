package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.SearchAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.exceptions.InstagramAPIException
import org.json.JSONArray

class Search internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a profile search API request.
     *
     * @param query The search term that is being queried.
     * @return  A {@link SyntheticResponse.ProfileSearch} object.
     */
    fun search(query: String): SyntheticResponse.ProfileSearch {
        val (res, error) = wrapAPIException { SearchAPI.search(query, Instagram.session) }

        res ?: return SyntheticResponse.ProfileSearch.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                val profiles = res.jsonObject.optJSONArray("users") ?: JSONArray()

                if (profiles.length() == 0) {
                    SyntheticResponse.ProfileSearch.Failure(InstagramAPIException(res.statusCode, Errors.ERROR_SEARCH_NO_RESULTS))
                } else {
                    SyntheticResponse.ProfileSearch.Success(profiles)
                }
            }
            else -> SyntheticResponse.ProfileSearch.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }
}
