package io.karn.instagram.ext

import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.endpoints.Stories
import io.reactivex.Single

fun Stories.getStories(primaryKey: String): Single<SyntheticResponse.StoryReelResult> {
    return Single.create { it.onSuccess(this.getStories(primaryKey)) }
}