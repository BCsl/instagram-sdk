package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.common.Errors
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import khttp.get
import org.json.JSONArray

class Account internal constructor() {

    companion object {
        internal const val RELATIONSHIP_FOLLOWERS = "followers"
        internal const val RELATIONSHIP_FOLLOWING = "following"
    }

    /**
     * Create an observable of an Instagram profile API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @return  An observable which emits a generic success/fail SyntheticResponse.
     */
    fun getAccount(primaryKey: String): SyntheticResponse = get(url = String.format(Endpoints.ACCOUNT_INFO, primaryKey),
            headers = Crypto.HEADERS,
            cookies = Instagram.getDefaultInstance().session.cookieJar)
            .let {
                return@let when (it.statusCode) {
                    200 -> SyntheticResponse.Success(it.jsonObject.get("user").toString())
                    else -> SyntheticResponse.Failure(String.format(Errors.ERROR_ACCOUNT_FETCH, it.statusCode, it.text))
                }
            }

    /**
     * Creates an observable of an Instagram profile feed API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @param maxId         The pagination ID to complete the API request.
     * @param minTimestamp  The timestamp at which to begin the fetch of the feed.
     * @return  An observable which emits a SyntheticResponse.ProfileFeedResult object.
     */
    fun getFeed(primaryKey: String, maxId: String = "", minTimestamp: String = ""): SyntheticResponse.ProfileFeedResult {
        return get(url = String.format(Endpoints.ACCOUNT_FEED, primaryKey, maxId, minTimestamp, "${Instagram.getDefaultInstance().session.pk}_${Instagram.getDefaultInstance().session.uuid}"),
                headers = Crypto.HEADERS,
                cookies = Instagram.getDefaultInstance().session.cookieJar)
                .let {
                    return@let when (it.statusCode) {
                        200 -> SyntheticResponse.ProfileFeedResult.Success(it.jsonObject.optJSONArray("items")
                                ?: JSONArray())
                        else -> SyntheticResponse.ProfileFeedResult.Failure(String.format(Errors.ERROR_FEED_FETCH, it.statusCode, it.text))
                    }
                }
    }

    fun getRelationship(relationship: String, userId: String, maxId: String): SyntheticResponse.RelationshipFetchResult {
        val endpoint = when (relationship) {
            RELATIONSHIP_FOLLOWERS -> Endpoints.FOLLOWERS
            else -> Endpoints.FOLLOWING
        }

        // Synchronous call.
        val response = khttp.get(url = String.format(endpoint, userId, "${Instagram.getDefaultInstance().session.pk}_${Instagram.getDefaultInstance().session.uuid}", maxId),
                headers = Crypto.HEADERS,
                cookies = Instagram.getDefaultInstance().session.cookieJar)

        if (response.statusCode != 200) {
            return SyntheticResponse.RelationshipFetchResult.Failure("Status Code: ${response.statusCode}, Message: ${response.text}.")
        }

        val jsonData = response.jsonObject.getJSONArray("users") ?: JSONArray()
        val nextMaxId = response.jsonObject.optString("next_max_id", "")

        return SyntheticResponse.RelationshipFetchResult.Success(nextMaxId, jsonData)
    }
}
