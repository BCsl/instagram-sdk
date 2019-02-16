package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object MediaAPI {
    fun getLikes(mediaKey: String, session: Session) =
            get(url = String.format(Endpoints.MEDIA_LIKES, mediaKey),
                    headers = Crypto.HEADERS,
                    cookies = session.cookieJar)

    fun getComments(mediaKey: String, session: Session) =
            get(url = String.format(Endpoints.MEDIA_COMMENTS, mediaKey),
                    headers = Crypto.HEADERS,
                    cookies = session.cookieJar)
}
