package io.karn.instagram.core

import khttp.structures.cookie.CookieJar

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