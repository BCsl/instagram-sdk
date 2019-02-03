package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.AccountAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import org.json.JSONArray
import org.json.JSONObject

class Account internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a profile API request.
     *
     * @param userKey    The Primary Key associated with the profile.
     * @return  A {@link SyntheticResponse.AccountDetails} object.
     */
    fun getAccount(userKey: String): SyntheticResponse.AccountDetails =
            AccountAPI.accountInfo(userKey, Instagram.session)
                    .let {
                        return@let when (it.statusCode) {
                            200 -> SyntheticResponse.AccountDetails.Success(it.jsonObject.optJSONObject("user")
                                    ?: JSONObject())
                            else -> SyntheticResponse.AccountDetails.Failure(String.format(Errors.ERROR_ACCOUNT_FETCH, it.statusCode, it.text))
                        }
                    }

    /**
     * Creates a SyntheticResponse from the response of a feed API request.
     *
     * @param userKey       The User Key associated with the profile.
     * @param maxId         The pagination ID to complete the API request.
     * @param minTimestamp  The timestamp at which to begin the fetch of the feed.
     * @return  A {@link SyntheticResponse.ProfileFeed} object.
     */
    fun getFeed(userKey: String, maxId: String = "", minTimestamp: String = ""): SyntheticResponse.ProfileFeed =
            AccountAPI.feed(userKey, maxId, minTimestamp, Instagram.session)
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
     * @param userKey   The User Key of the profile of with the relationship is being queried.
     * @param maxId     The pagination ID to complete the API request.
     * @return  A {@link SyntheticResponse.Relationships} object.
     */
    fun getFollowers(userKey: String, maxId: String = ""): SyntheticResponse.Relationships =
            getRelationship(Endpoints.FOLLOWERS, userKey, maxId)

    /**
     * Creates a SyntheticResponse from the response of a following relationship API request.
     *
     * @param userKey   The User Key of the profile of with the relationship is being queried.
     * @param maxId     The pagination ID to complete the API request.
     * @return  A {@link SyntheticResponse.Relationships} object.
     */
    fun getFollowing(userKey: String, maxId: String = ""): SyntheticResponse.Relationships =
            getRelationship(Endpoints.FOLLOWING, userKey, maxId)

    private fun getRelationship(endpoint: String, primaryKey: String, maxId: String): SyntheticResponse.Relationships =
            AccountAPI.relationship(endpoint, primaryKey, maxId, Instagram.session)
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
