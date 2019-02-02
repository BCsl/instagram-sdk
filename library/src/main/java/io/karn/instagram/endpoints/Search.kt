package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.common.Errors
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import khttp.get
import org.json.JSONArray

class Search internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a profile search API request.
     *
     * @param query The search term that is being queried.
     * @return  A {@link SyntheticResponse.ProfileSearch} object.
     */
    fun search(query: String): SyntheticResponse.ProfileSearch = get(url = String.format(Endpoints.SEARCH, "${Instagram.getInstance().session.pk}_${Instagram.getInstance().session.uuid}", query),
            headers = Crypto.HEADERS,
            cookies = Instagram.getInstance().session.cookieJar)
            .let {
                return@let when (it.statusCode) {
                    200 -> {
                        val profiles = it.jsonObject.optJSONArray("users") ?: JSONArray()

                        if (profiles.length() == 0) {
                            SyntheticResponse.ProfileSearch.Failure(Errors.ERROR_SEARCH_NO_RESULTS)
                        } else {
                            SyntheticResponse.ProfileSearch.Success(profiles)
                        }
                    }
                    else -> SyntheticResponse.ProfileSearch.Failure(String.format(Errors.ERROR_INCOMPLETE_SEARCH, it.statusCode, it.text))
                }
            }
}
