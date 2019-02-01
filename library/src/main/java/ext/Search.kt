package ext

import core.SyntheticResponse
import endpoints.Search
import io.reactivex.Single

fun Search.seachSingle(query: String): Single<SyntheticResponse.ProfileSearchResult> {
    return Single.create { it.onSuccess(this.search(query)) }
}