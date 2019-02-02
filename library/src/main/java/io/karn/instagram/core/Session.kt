package io.karn.instagram.core

import khttp.structures.cookie.CookieJar

/**
 * The Session class maintains the Session metadata for the current instance of the library.
 */
class Session {
    var pk: String? = null
    var uuid: String? = null

    var cookieJar: CookieJar? = null

    fun loadAuthData(userPk: String?, uuid: String?, cookie: String) {
        this.pk = userPk
        this.uuid = uuid
        this.cookieJar = Crypto.deserializeCookies(cookie)
    }
}