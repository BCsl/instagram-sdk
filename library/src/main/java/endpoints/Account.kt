package endpoints

import Instagram
import common.Errors
import core.Crypto
import core.Endpoints
import core.SyntheticResponse
import io.reactivex.Single
import khttp.extensions.get
import org.json.JSONArray
import java.io.IOException

class Account internal constructor() {

    companion object {
        private const val RELATIONSHIP_FOLLOWERS = "followers"
        private const val RELATIONSHIP_FOLLOWING = "following"
    }

    /**
     * Create an observable of an Instagram profile API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @return  An observable which emits a generic success/fail SyntheticResponse.
     */
    fun getAccountObservable(primaryKey: String): Single<SyntheticResponse> {
        return get(url = String.format(Endpoints.ACCOUNT_INFO, primaryKey),
                headers = Crypto.HEADERS,
                cookies = Instagram.getDefaultInstance().session.cookieJar)
                .map {
                    return@map when (it.statusCode) {
                        200 -> SyntheticResponse.Success(it.jsonObject.get("user").toString())
                        else -> SyntheticResponse.Failure(String.format(Errors.ERROR_ACCOUNT_FETCH, it.statusCode, it.text))
                    }
                }
                .onErrorReturn { SyntheticResponse.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
    }

    /**
     * Creates an observable of an Instagram profile feed API request.
     *
     * @param primaryKey    The Primary Key associated with the profile.
     * @param maxId         The pagination ID to complete the API request.
     * @param minTimestamp  The timestamp at which to begin the fetch of the feed.
     * @return  An observable which emits a SyntheticResponse.ProfileFeedResult object.
     */
    fun getFeed(primaryKey: String, maxId: String = "", minTimestamp: String = ""): Single<SyntheticResponse.ProfileFeedResult> {
        return get(url = String.format(Endpoints.ACCOUNT_FEED, primaryKey, maxId, minTimestamp, "${Instagram.getDefaultInstance().session.pk}_${Instagram.getDefaultInstance().session.uuid}"),
                headers = Crypto.HEADERS,
                cookies = Instagram.getDefaultInstance().session.cookieJar)
                .map {
                    return@map when (it.statusCode) {
                        200 -> SyntheticResponse.ProfileFeedResult.Success(it.jsonObject.optJSONArray("items")
                                ?: JSONArray())
                        else -> SyntheticResponse.ProfileFeedResult.Failure(String.format(Errors.ERROR_FEED_FETCH, it.statusCode, it.text))
                    }
                }
                .onErrorReturn {
                    SyntheticResponse.ProfileFeedResult.Failure(it.message ?: Errors.ERROR_UNKNOWN)
                }
    }

    fun getFollowersObservable(userId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
        return getRelationship(RELATIONSHIP_FOLLOWERS, userId, reducer).onErrorReturn { "" }
    }

    fun getFollowingObservable(userId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
        return getRelationship(RELATIONSHIP_FOLLOWING, userId, reducer).onErrorReturn { "" }
    }

    private fun getRelationship(relationship: String, userId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
        return getRelationshipObservable(relationship, userId, "", reducer)
    }

    private fun getRelationship(relationship: String, userId: String, maxId: String): SyntheticResponse.RelationshipFetchResult {
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

    private fun getRelationshipObservable(relationship: String, userId: String, maxId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
        return Single.create<String> { emitter ->

            var nextMaxId = maxId
            var totalProfilesFound = 0

            do {
                val result = getRelationship(relationship, userId, nextMaxId)

                // Propagate to handler.
                reducer(result)

                nextMaxId = when (result) {
                    is SyntheticResponse.RelationshipFetchResult.Failure -> ""
                    is SyntheticResponse.RelationshipFetchResult.Success -> {
                        totalProfilesFound += result.relationships.length()

                        if (totalProfilesFound >= Instagram.getDefaultInstance().configuration.maxRelationshipFetch)
                            ""
                        else
                            result.nextMaxId.takeIf { it != "null" } ?: ""
                    }
                }
            } while (!nextMaxId.isEmpty())

            // This is a hacky way of doing this, investigate further.
            try {
                emitter.onSuccess(nextMaxId)
            } catch (ex: IOException) {
                // Prevents undeliverable exception
                emitter.tryOnError(ex)
            }
        }.onErrorReturn {
            reducer(SyntheticResponse.RelationshipFetchResult.Failure(it.message ?: Errors.ERROR_UNKNOWN))

            ""
        }
    }

}
