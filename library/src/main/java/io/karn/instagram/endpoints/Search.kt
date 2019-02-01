package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.common.Errors
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import khttp.get
import org.json.JSONArray

class Search {

    companion object {
        private const val MEMBER_NAME_USERS = "users"
        const val MAX_SUGGESTED_RESULTS = 10
    }

    /**
     * Create an observable of an Instagram profile search API request.
     *
     * @param query The search term that is being queried.
     * @return  An observable that emits a SyntheticResponse.ProfileSearchResult object.
     */
    fun search(query: String): SyntheticResponse.ProfileSearchResult = get(url = String.format(Endpoints.SEARCH, "${Instagram.getInstance().session.pk}_${Instagram.getInstance().session.uuid}", query),
            headers = Crypto.HEADERS,
            cookies = Instagram.getInstance().session.cookieJar)
            .let {
                return@let when (it.statusCode) {
                    200 -> {
                        val profiles = it.jsonObject.optJSONArray(MEMBER_NAME_USERS) ?: JSONArray()

                        if (profiles.length() == 0) {
                            SyntheticResponse.ProfileSearchResult.Failure(Errors.ERROR_SEARCH_NO_RESULTS)
                        } else {
                            SyntheticResponse.ProfileSearchResult.Success(profiles)
                        }
                    }
                    else -> SyntheticResponse.ProfileSearchResult.Failure(String.format(Errors.ERROR_INCOMPLETE_SEARCH, it.statusCode, it.text))
                }
            }
}
