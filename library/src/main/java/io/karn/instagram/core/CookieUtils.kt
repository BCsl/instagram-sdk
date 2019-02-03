package io.karn.instagram.core

import khttp.structures.cookie.Cookie
import khttp.structures.cookie.CookieJar
import org.json.JSONArray

object CookieUtils {

    fun serializeToJson(cookieJar: CookieJar): JSONArray {
        val json = JSONArray()

        cookieJar.forEach { json.put(it.toString()) }

        return json
    }

    fun deserializeFromJson(cookieJson: JSONArray): CookieJar {
        val cookieJar = CookieJar()

        (0 until cookieJson.length()).forEach { cookieJar.setCookie(Cookie(cookieJson.getString(it))) }

        return cookieJar
    }
}