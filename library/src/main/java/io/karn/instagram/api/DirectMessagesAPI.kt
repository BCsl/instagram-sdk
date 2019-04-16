package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get


internal object DirectMessagesAPI {

    fun getMessages(maxID: String, session: Session) =
            get(url = String.format(Endpoints.DIRECT_MESSAGES, maxID),
                    headers = Crypto.HEADERS,
                    cookies = session.cookieJar)
}
