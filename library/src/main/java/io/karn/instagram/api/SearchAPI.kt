package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object SearchAPI {

    fun search(query: String, session: Session) =
            get(url = String.format(Endpoints.SEARCH, "${session.primaryKey}_${session.uuid}", query),
                    headers = Crypto.HEADERS,
                    cookies = session.cookieJar)
}
