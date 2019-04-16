package io.karn.instagram.api

import io.karn.instagram.core.Crypto
import io.karn.instagram.core.Endpoints
import io.karn.instagram.core.Session
import io.karn.instagram.endpoints.Authentication
import khttp.get
import khttp.post
import khttp.responses.Response

internal object AuthenticationAPI {

    fun getTokenForAuth(): Response {
        return get(url = String.format(Endpoints.CSRF_TOKEN, Crypto.generateUUID(false)),
                headers = Crypto.HEADERS,
                allowRedirects = true)
    }

    fun login(data: String): Response {
        return post(url = Endpoints.LOGIN,
                headers = Crypto.HEADERS,
                allowRedirects = true,
                data = data)
    }

    fun twoFactor(data: String): Response {
        return post(url = Endpoints.LOGIN_APPROVAL,
                headers = Crypto.HEADERS,
                data = data)
    }

    fun prepareAuthChallenge(challengePath: String, session: Session): Response {
        return get(url = String.format(Endpoints.CHALLENGE_PATH, challengePath),
                headers = Crypto.HEADERS,
                cookies = session.cookieJar)
    }

    fun selectAuthChallengeMethod(challengePath: String, method: String, session: Session): Response {
        return post(url = String.format(Endpoints.CHALLENGE_PATH, challengePath),
                headers = Crypto.HEADERS,
                cookies = session.cookieJar,
                data = hashMapOf("choice" to if (Authentication.AUTH_METHOD_PHONE == method) 0 else 1))
    }

    fun submitAuthChallenge(challengePath: String, code: String, session: Session): Response {
        return post(url = String.format(Endpoints.CHALLENGE_PATH, challengePath),
                headers = mapOf("User-Agent" to Crypto.buildUserAgent()),
                cookies = session.cookieJar,
                data = hashMapOf("security_code" to Integer.parseInt(code)))
    }

    fun logout(): Response {
        return get(url = Endpoints.LOGOUT, headers = Crypto.HEADERS)
    }

    internal fun parseCSRFToken(response: Response): String? = response.cookies.getCookie("csrftoken")?.value?.toString()
}
