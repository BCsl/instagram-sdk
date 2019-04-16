package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.AccountAPI
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.exceptions.InstagramAPIException
import org.json.JSONArray
import org.json.JSONObject

class Account internal constructor() {

    /**
     * Create a SyntheticResponse from the response of a profile API request.
     *
     * @param userKey    The Primary Key associated with the profile.
     * @return  A {@link SyntheticResponse.AccountDetails} object.
     */
    fun getAccount(userKey: String): SyntheticResponse.AccountDetails {
        val (res, error) = wrapAPIException { AccountAPI.accountInfo(userKey, Instagram.session) }

        res ?: return SyntheticResponse.AccountDetails.Failure(error!!)

        // Handle error messages.
        return when (res.statusCode) {
            200 -> SyntheticResponse.AccountDetails.Success(res.jsonObject.optJSONObject("user") ?: JSONObject())
            else -> SyntheticResponse.AccountDetails.Failure(InstagramAPIException(res.statusCode, res.text))
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
    fun getFeed(userKey: String, maxId: String = "", minTimestamp: String = ""): SyntheticResponse.ProfileFeed {
        val (res, error) = wrapAPIException { AccountAPI.feed(userKey, maxId, minTimestamp, Instagram.session) }

        res ?: return SyntheticResponse.ProfileFeed.Failure(error!!)

        return when (res.statusCode) {
            200 -> SyntheticResponse.ProfileFeed.Success(res.jsonObject.optString("next_max_id", ""),res.jsonObject.optJSONArray("items") ?: JSONArray())
            else -> SyntheticResponse.ProfileFeed.Failure(InstagramAPIException(res.statusCode, res.text))
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

    private fun getRelationship(endpoint: String, primaryKey: String, maxId: String): SyntheticResponse.Relationships {
        val (res, error) = wrapAPIException { AccountAPI.relationships(endpoint, primaryKey, maxId, Instagram.session) }

        res ?: return SyntheticResponse.Relationships.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                val jsonData = res.jsonObject.getJSONArray("users") ?: JSONArray()
                val nextMaxId = res.jsonObject.optString("next_max_id", "")

                SyntheticResponse.Relationships.Success(nextMaxId, jsonData)
            }
            else -> SyntheticResponse.Relationships.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }

    /**
     * Creates a SyntheticResponse from the response of a user follow API request.
     *
     * @return  A {@link SyntheticResponse.RelationshipUpdate} object.
     */
    fun followProfile(userKey: String): SyntheticResponse.RelationshipUpdate =
            updateRelationship(Endpoints.FOLLOW, userKey)

    /**
     * Creates a SyntheticResponse from the response of a user follow API request.
     *
     * @return  A {@link SyntheticResponse.RelationshipUpdate} object.
     */
    fun unfollowProfile(userKey: String): SyntheticResponse.RelationshipUpdate =
            updateRelationship(Endpoints.UNFOLLOW, userKey)

    private fun updateRelationship(endpoint: String, userKey: String): SyntheticResponse.RelationshipUpdate {
        val (res, error) = wrapAPIException { AccountAPI.updateRelationship(endpoint, userKey, Instagram.session) }

        res ?: return SyntheticResponse.RelationshipUpdate.Failure(error!!)

        return when (res.statusCode) {
            200 -> SyntheticResponse.RelationshipUpdate.Success(res.jsonObject.optJSONObject("friendship_status") ?: JSONObject())
            else -> SyntheticResponse.RelationshipUpdate.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }

    /**
     * Creates a SyntheticResponse from the response of a blocked user list API request.
     *
     * @return  A {@link SyntheticResponse.Blocks} object.
     */
    fun getBlocked(): SyntheticResponse.Blocks {
        val (res, error) = wrapAPIException { AccountAPI.blockedAccounts(Instagram.session) }

        res ?: return SyntheticResponse.Blocks.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                SyntheticResponse.Blocks.Success(res.jsonObject.optJSONArray("blocked_list") ?: JSONArray())
            }
            else -> SyntheticResponse.Blocks.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }
}
