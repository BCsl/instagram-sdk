package endpoints

import Instagram
import android.text.TextUtils
import common.Errors
import core.Crypto
import core.Endpoints
import core.SyntheticResponse
import khttp.get
import khttp.post
import khttp.responses.Response
import khttp.structures.cookie.CookieJar
import org.json.JSONObject

/**
 * Login Controller.
 * =================
 *
 * The InstagramUtils API works in a very particular manner. At the high level below is the process.
 *
 * 1. Fetch A CSRF Token
 *      This is achieved by placing a GET request to
 */
class Authentication {

    companion object {
        private const val MEMBER_COOKIE = "cookie"

        const val AUTH_METHOD_EMAIL = "email"
        const val AUTH_METHOD_PHONE = "phone"
    }

    fun authenticate(username: String, password: String, token: String? = null): SyntheticResponse.AuthenticationResult {
        if (!TextUtils.isEmpty(token)) {
            // Go straight to login.
            return processLogin(username, password, token!!)
        }

        return get(url = String.format(Endpoints.CSRF_TOKEN, Crypto.generateUUID(false)),
                headers = Crypto.HEADERS,
                allowRedirects = true)
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> {
                            val csrfToken = parseCSRFToken(response)
                            processLogin(username, password, csrfToken)
                        }
                        else -> SyntheticResponse.AuthenticationResult.TokenFailure(response.statusCode, response.jsonObject)
                    }
                }
    }

    private fun processLogin(username: String, password: String, token: String): SyntheticResponse.AuthenticationResult {
        Instagram.getDefaultInstance().session.uuid = Crypto.generateUUID(true)

        // Generate the login payload.
        val deviceId = Crypto.generateDeviceId(username, password)
        val data = Crypto.generateLoginPayload(token, username, password, 0, deviceId)

        return post(url = Endpoints.LOGIN,
                headers = Crypto.HEADERS,
                allowRedirects = true,
                data = data)
                .let {
                    return@let when (it.statusCode) {
                        200 -> {
                            val auth = it.jsonObject
                            auth.put(MEMBER_COOKIE, Crypto.serializeCookies(it.cookies))

                            SyntheticResponse.AuthenticationResult.Success(auth)
                        }
                        400 -> {
                            Instagram.getDefaultInstance().session.cookieJar = it.cookies

                            val result: SyntheticResponse.AuthenticationResult = when {
                                it.jsonObject.optBoolean("two_factor_required") -> {
                                    // User requires two factor.
                                    SyntheticResponse.AuthenticationResult.TwoFactorAuth(it.jsonObject
                                            .put("token", token)
                                            .put("device_id", deviceId))
                                }
                                it.jsonObject.has("challenge") -> {
                                    // User needs to pass challenge
                                    SyntheticResponse.AuthenticationResult.AuthChallenge(it.jsonObject.getJSONObject("challenge"))
                                }
                                else -> SyntheticResponse.AuthenticationResult.InvalidCredentials(it.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                            }

                            result
                        }
                        else -> SyntheticResponse.AuthenticationResult.ApiFailure(it.statusCode, it.jsonObject)
                    }
                }
    }

    fun twoFactorLogin(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): SyntheticResponse.TwoFactorAuthResult {
        val data = Crypto.generateTwoFactorPayload(code.replace("\\s".toRegex(), ""), identifier, token, username, password, deviceId)

        return post(url = Endpoints.LOGIN_APPROVAL,
                headers = Crypto.HEADERS,
                data = data)
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> {
                            val auth = response.jsonObject
                            auth.put(MEMBER_COOKIE, Crypto.serializeCookies(response.cookies))

                            SyntheticResponse.TwoFactorAuthResult.Success(auth)
                        }
                        else -> SyntheticResponse.TwoFactorAuthResult.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    fun prepareAuthChallenge(path: String): SyntheticResponse.AuthChallengeResult {
        return get(url = String.format(Endpoints.CHALLENGE_PATH, path),
                cookies = Instagram.getDefaultInstance().session.cookieJar ?: CookieJar(),
                headers = Crypto.HEADERS)
                .let {
                    return@let when (it.statusCode) {
                        200 -> {
                            Instagram.getDefaultInstance().session.cookieJar = it.cookies

                            if (it.jsonObject.optString("step_name") == "select_verify_method") {
                                SyntheticResponse.AuthChallengeResult.Success(it.jsonObject)
                            } else {
                                SyntheticResponse.AuthChallengeResult.Failure(it.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                            }
                        }
                        else -> SyntheticResponse.AuthChallengeResult.Failure(it.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    fun selectAuthChallengeMethod(path: String, method: String): SyntheticResponse.AuthMethodSelectedResult {
        return post(url = String.format(Endpoints.CHALLENGE_PATH, path),
                cookies = Instagram.getDefaultInstance().session.cookieJar ?: CookieJar(),
                headers = Crypto.HEADERS,
                data = hashMapOf("choice" to if (AUTH_METHOD_PHONE == method) 0 else 1))
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> {
                            Instagram.getDefaultInstance().session.cookieJar = response.cookies

                            when (response.jsonObject.optString("step_name")) {
                                "verify_code" -> SyntheticResponse.AuthMethodSelectedResult.PhoneSelectionSuccess(response.jsonObject.optJSONObject("step_data")
                                        ?: JSONObject())
                                "verify_email" -> SyntheticResponse.AuthMethodSelectedResult.EmailSelectionSuccess(response.jsonObject.optJSONObject("step_data")
                                        ?: JSONObject())
                                else -> SyntheticResponse.AuthMethodSelectedResult.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                            }
                        }
                        else -> SyntheticResponse.AuthMethodSelectedResult.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    fun submitChallengeCode(path: String, code: String): SyntheticResponse.ChallengeCodeSubmitResult {
        return post(url = String.format(Endpoints.CHALLENGE_PATH, path),
                cookies = Instagram.getDefaultInstance().session.cookieJar ?: CookieJar(),
                headers = mapOf("User-Agent" to Crypto.buildUserAgent()),
                data = hashMapOf("security_code" to Integer.parseInt(code)))
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> {
                            val token = parseCSRFToken(response)
                            SyntheticResponse.ChallengeCodeSubmitResult.Success(token)
                        }
                        else -> SyntheticResponse.ChallengeCodeSubmitResult.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    fun logoutUser(): SyntheticResponse {
        return get(url = Endpoints.LOGOUT,
                headers = Crypto.HEADERS)
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> SyntheticResponse.Success(Unit)
                        else -> SyntheticResponse.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    private fun parseCSRFToken(response: Response): String {
        return response.cookies.getCookie("csrftoken")?.value?.toString()
                ?: throw Errors.TokenFetchException(Errors.ERROR_INVALID_CSRF)
    }
}
