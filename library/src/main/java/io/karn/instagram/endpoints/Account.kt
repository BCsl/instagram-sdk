package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.common.Errors
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import khttp.get
import org.json.JSONArray
import org.json.JSONObject

class Account internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a profile API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @return  A {@link SyntheticResponse.AccountDetails} object.
     */
    fun getAccount(primaryKey: String): SyntheticResponse.AccountDetails = get(url = String.format(Endpoints.ACCOUNT_INFO, primaryKey),
            headers = Crypto.HEADERS,
            cookies = Instagram.getInstance().session.cookieJar)
            .let {
                return@let when (it.statusCode) {
                    200 -> SyntheticResponse.AccountDetails.Success(it.jsonObject.optJSONObject("user") ?: JSONObject())
                    else -> SyntheticResponse.AccountDetails.Failure(String.format(Errors.ERROR_ACCOUNT_FETCH, it.statusCode, it.text))
                }
            }

    /**
     * Creates a SyntheticResponse from the response of a feed API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @param maxId         The pagination ID to complete the API request.
     * @param minTimestamp  The timestamp at which to begin the fetch of the feed.
     * @return  A {@link SyntheticResponse.ProfileFeed} object.
     */
    fun getFeed(primaryKey: String, maxId: String = "", minTimestamp: String = ""): SyntheticResponse.ProfileFeed =
            get(url = String.format(Endpoints.ACCOUNT_FEED, primaryKey, maxId, minTimestamp, "${Instagram.getInstance().session.pk}_${Instagram.getInstance().session.uuid}"),
                    headers = Crypto.HEADERS,
                    cookies = Instagram.getInstance().session.cookieJar)
                    .let {
                        return@let when (it.statusCode) {
                            200 -> SyntheticResponse.ProfileFeed.Success(it.jsonObject.optJSONArray("items")
                                    ?: JSONArray())
                            else -> SyntheticResponse.ProfileFeed.Failure(String.format(Errors.ERROR_FEED_FETCH, it.statusCode, it.text))
                        }
                    }

    /**
     * Creates a SyntheticResponse from the response of a follower relationship API request.
     *
     * @param primaryKey    The Primary Key of the profile of with the relationship is being queried.
     * @param maxId         The pagination ID to complete the API request.
     * @return  A {@link SyntheticResponse.Relationships} object.
     */
    fun getFollowers(primaryKey: String, maxId: String = ""): SyntheticResponse.Relationships =
            getRelationship(Endpoints.FOLLOWERS, primaryKey, maxId)

    /**
     * Creates a SyntheticResponse from the response of a following relationship API request.
     *
     * @param primaryKey    The Primary Key of the profile of with the relationship is being queried.
     * @param maxId         The pagination ID to complete the API request.
     * @return  A {@link SyntheticResponse.Relationships} object.
     */
    fun getFollowing(primaryKey: String, maxId: String = ""): SyntheticResponse.Relationships =
            getRelationship(Endpoints.FOLLOWING, primaryKey, maxId)

    private fun getRelationship(endpoint: String, primaryKey: String, maxId: String): SyntheticResponse.Relationships =
            get(url = String.format(endpoint, primaryKey, "${Instagram.getInstance().session.pk}_${Instagram.getInstance().session.uuid}", maxId),
                    headers = Crypto.HEADERS,
                    cookies = Instagram.getInstance().session.cookieJar)
                    .let {
                        return@let when (it.statusCode) {
                            200 -> {
                                val jsonData = it.jsonObject.getJSONArray("users") ?: JSONArray()
                                val nextMaxId = it.jsonObject.optString("next_max_id", "")

                                SyntheticResponse.Relationships.Success(nextMaxId, jsonData)
                            }
                            else -> SyntheticResponse.Relationships.Failure("Status Code: ${it.statusCode}, Message: ${it.text}.")
                        }
                    }
}
