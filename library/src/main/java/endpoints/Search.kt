package endpoints

import Instagram
import common.Errors
import core.Crypto
import core.Endpoints
import core.SyntheticResponse
import io.reactivex.Single
import khttp.extensions.get
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
    fun search(query: String): Single<SyntheticResponse.ProfileSearchResult> {
        return get(url = String.format(Endpoints.SEARCH, "${Instagram.getDefaultInstance().session.pk}_${Instagram.getDefaultInstance().session.uuid}", query),
                headers = Crypto.HEADERS,
                cookies = Instagram.getDefaultInstance().session.cookieJar)
                .map {
                    return@map when (it.statusCode) {
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
}
