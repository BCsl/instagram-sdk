package ext

import Instagram
import common.Errors
import core.SyntheticResponse
import endpoints.Account
import io.reactivex.Single
import java.io.IOException

fun Account.getAccountSingle(primaryKey: String): Single<SyntheticResponse> {
    return Single.create<SyntheticResponse> { it.onSuccess(this.getAccount(primaryKey)) }
            .onErrorReturn { SyntheticResponse.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Account.getFeedSingle(primaryKey: String, maxId: String = "", minTimestamp: String = ""): Single<SyntheticResponse.ProfileFeedResult> {
    return Single.create<SyntheticResponse.ProfileFeedResult> { it.onSuccess(this.getFeed(primaryKey, maxId, minTimestamp)) }
            .onErrorReturn {
                SyntheticResponse.ProfileFeedResult.Failure(it.message ?: Errors.ERROR_UNKNOWN)
            }
}

fun Account.getFollowersSingle(userId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
    return getRelationshipSingle(Account.RELATIONSHIP_FOLLOWERS, userId, "", reducer).onErrorReturn { "" }
}

fun Account.getFollowingSingle(userId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
    return getRelationshipSingle(Account.RELATIONSHIP_FOLLOWING, userId, "", reducer).onErrorReturn { "" }
}

private fun Account.getRelationshipSingle(relationship: String, userId: String, maxId: String, reducer: (SyntheticResponse.RelationshipFetchResult) -> Unit): Single<String> {
    return Single.create<String> { emitter ->

        var nextMaxId = maxId
        var totalProfilesFound = 0

        do {
            val result = this.getRelationship(relationship, userId, nextMaxId)

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