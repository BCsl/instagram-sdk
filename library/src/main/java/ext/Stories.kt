package ext

import core.SyntheticResponse
import endpoints.Stories
import io.reactivex.Single

fun Stories.getStoriesSingle(primaryKey: String): Single<SyntheticResponse.StoryReelResult> {
    return Single.create { it.onSuccess(this.getStories(primaryKey)) }
}