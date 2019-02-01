package io.karn.instagram.ext

import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.endpoints.Search
import io.reactivex.Single

fun Search.seachSingle(query: String): Single<SyntheticResponse.ProfileSearchResult> {
    return Single.create { it.onSuccess(this.search(query)) }
}