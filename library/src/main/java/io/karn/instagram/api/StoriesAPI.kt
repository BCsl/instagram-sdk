package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object StoriesAPI {

    fun getStories(primaryKey: String, session: Session) =
        get(url = String.format(Endpoints.STORIES, primaryKey),
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)
}
