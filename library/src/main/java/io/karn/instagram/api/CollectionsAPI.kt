package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object CollectionsAPI {

    fun listCollections(session: Session) =
        get(url = Endpoints.COLLECTIONS_LIST,
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)

    fun getCollection(collectionId: String, session: Session) =
        get(url = "${Endpoints.COLLECTIONS_LIST}$collectionId",
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)
}
