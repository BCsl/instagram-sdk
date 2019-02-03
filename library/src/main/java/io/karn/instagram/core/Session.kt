package io.karn.instagram.core

import khttp.structures.cookie.CookieJar
import org.json.JSONArray

/**
 * The Session class maintains the Session metadata for the current instance of the library.
 */
data class Session internal constructor(
        internal val primaryKey: String = "",
        internal var uuid: String = "",
        internal var cookieJar: CookieJar = CookieJar()
) {
    companion object {
        fun buildSession(primaryKey: String, uuid: String, cookies: String): Session {
            return Session(primaryKey, uuid, CookieUtils.deserializeFromJson(JSONArray(cookies)))
        }
    }
}