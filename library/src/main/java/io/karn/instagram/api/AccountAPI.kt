package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import khttp.get

internal object AccountAPI {

    fun accountInfo(primaryKey: String, session: Session) =
        get(url = String.format(Endpoints.ACCOUNT_INFO, primaryKey),
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)

    fun feed(feedUserKey: String, maxId: String, minTimestamp: String, session: Session) =
        get(url = String.format(Endpoints.ACCOUNT_FEED, feedUserKey, maxId, minTimestamp, "${session.primaryKey}_${session.uuid}"),
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)

    fun relationship(endpoint: String, userKey: String, maxId: String, session: Session) =
        get(url = String.format(endpoint, userKey, "${session.primaryKey}_${session.uuid}", maxId),
            headers = Crypto.HEADERS,
            cookies = session.cookieJar)
}
