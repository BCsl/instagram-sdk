package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.AuthenticationAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.CookieUtils
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.SyntheticResponse
import org.json.JSONObject

/**
 * Authentication
 *
 * Instagram authentication uses a cookie based authentication mechanism in addition to the CSRF token. Below are the
 * Steps as well as the corresponding SDK functions which perform said step.
 *
 * 1. Admit one - Fetching the original token.
 *   The first step in the authentication process is to retrieve a token with can be used to authenticate. This token is
 *   then used, along with the account credentials to sign the user in.
 *
 *   We will defer to the next step for the SDK function which handles these together.
 *
 * 2. Authentication
 *   The next step is to authenticate the user. The corresponding SDK function is [Authentication.authenticate].
 *
 *   The resulting SyntheticResponse maps the following states:
 *   - Success -> The account has been authenticated, the result contains the serialized Cookies as well as the UUID used.
 *                  The Cookies and UUID are used to restore the session after a cold start via [Session.buildSession].
 *   - TwoFactorAuth -> The account has been authenticated but requires a two-factor code to authorize the login. A code
 *                      will be sent to the primary two-factor method (phone number/email) associated with the account.
 *                      The authentication can be completed via the [Authentication.twoFactor] function.
 *   - ChallengeRequired -> The account has been flagged by the server for a suspicious login, a verification flow needs
 *                          to be followed. The path for the challenge is provided and must be queried to
 *   - InvalidCredentials ->
 *   - ApiFailure ->
 */
class Authentication internal constructor() {

    companion object {
        const val AUTH_METHOD_EMAIL = "email"
        const val AUTH_METHOD_PHONE = "phone"
    }

    fun authenticate(username: String, password: String, token: String? = null): SyntheticResponse.AuthenticationResult {
        if (!token.isNullOrBlank()) {
            // Go straight to login.
            return processLogin(username, password, token)
        }

        val res = wrapAPIException { AuthenticationAPI.getTokenForAuth() }

        return when (res.statusCode) {
            200 -> {
                val newToken = AuthenticationAPI.parseCSRFToken(res).takeIf { !it.isNullOrBlank() || it != "null" }
                        ?: return SyntheticResponse.AuthenticationResult.TokenFailure(412, res.jsonObject)

                processLogin(username, password, newToken)
            }
            else -> SyntheticResponse.AuthenticationResult.TokenFailure(res.statusCode, res.jsonObject)
        }
    }

    fun twoFactorLogin(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): SyntheticResponse.TwoFactorAuthResult {
        val data = Crypto.generateTwoFactorPayload(code.replace("\\s".toRegex(), ""), identifier, token, username, password, deviceId)

        val res = wrapAPIException { AuthenticationAPI.twoFactor(data) }

        return when (res.statusCode) {
            200 -> {
                val auth = res.jsonObject
                auth.put("cookie", CookieUtils.serializeToJson(res.cookies))
                auth.put("uuid", Instagram.session.uuid)

                SyntheticResponse.TwoFactorAuthResult.Success(auth)
            }
            else -> SyntheticResponse.TwoFactorAuthResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
        }
    }

    fun prepareAuthChallenge(path: String): SyntheticResponse.AuthChallengeResult {
        val res = wrapAPIException { AuthenticationAPI.prepareAuthChallenge(path, Instagram.session) }

        return when (res.statusCode) {
            200 -> {
                Instagram.session.cookieJar = res.cookies

                if (res.jsonObject.optString("step_name") == "select_verify_method") {
                    SyntheticResponse.AuthChallengeResult.Success(res.jsonObject)
                } else {
                    SyntheticResponse.AuthChallengeResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                }
            }
            else -> SyntheticResponse.AuthChallengeResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
        }
    }

    fun selectAuthChallengeMethod(path: String, method: String): SyntheticResponse.AuthMethodSelectedResult {
        val res = wrapAPIException { AuthenticationAPI.selectAuthChallengeMethod(path, method, Instagram.session) }

        return when (res.statusCode) {
            200 -> {
                Instagram.session.cookieJar = res.cookies

                when (res.jsonObject.optString("step_name")) {
                    "verify_code" -> SyntheticResponse.AuthMethodSelectedResult.PhoneSelectionSuccess(res.jsonObject.optJSONObject("step_data")
                            ?: JSONObject())
                    "verify_email" -> SyntheticResponse.AuthMethodSelectedResult.EmailSelectionSuccess(res.jsonObject.optJSONObject("step_data")
                            ?: JSONObject())
                    else -> SyntheticResponse.AuthMethodSelectedResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                }
            }
            else -> SyntheticResponse.AuthMethodSelectedResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
        }
    }

    fun submitChallengeCode(path: String, code: String): SyntheticResponse.ChallengeCodeSubmitResult {
        val res = wrapAPIException { AuthenticationAPI.submitAuthChallenge(path, code, Instagram.session) }

        return when (res.statusCode) {
            200 -> {
                val token = AuthenticationAPI.parseCSRFToken(res).takeIf { !it.isNullOrBlank() || it != "null" }
                        ?: return SyntheticResponse.ChallengeCodeSubmitResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))

                SyntheticResponse.ChallengeCodeSubmitResult.Success(token)
            }
            else -> SyntheticResponse.ChallengeCodeSubmitResult.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
        }
    }

    fun logoutUser(): SyntheticResponse.Logout {
        val res = wrapAPIException { AuthenticationAPI.logout() }

        return when (res.statusCode) {
            200 -> SyntheticResponse.Logout.Success(res.statusCode)
            else -> SyntheticResponse.Logout.Failure(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
        }
    }

    private fun processLogin(username: String, password: String, token: String): SyntheticResponse.AuthenticationResult {
        Instagram.session.uuid = Crypto.generateUUID(true)

        // Generate the login payload.
        val deviceId = Crypto.generateDeviceId(username, password)
        val data = Crypto.generateLoginPayload(token, username, password, 0, deviceId)

        val res = wrapAPIException { AuthenticationAPI.login(data) }

        return when (res.statusCode) {
            200 -> {
                Instagram.session.cookieJar = res.cookies

                val auth = res.jsonObject

                auth.put("cookie", CookieUtils.serializeToJson(res.cookies))
                auth.put("uuid", Instagram.session.uuid)

                SyntheticResponse.AuthenticationResult.Success(auth)
            }
            400 -> {
                Instagram.session.cookieJar = res.cookies

                when {
                    res.jsonObject.optBoolean("two_factor_required") -> {
                        // User requires two factor.
                        SyntheticResponse.AuthenticationResult.TwoFactorAuth(res.jsonObject
                                .put("token", token)
                                .put("device_id", deviceId))
                    }
                    res.jsonObject.has("challenge") -> {
                        // User needs to pass challenge
                        SyntheticResponse.AuthenticationResult.AuthChallenge(res.jsonObject.getJSONObject("challenge"))
                    }
                    else -> SyntheticResponse.AuthenticationResult.InvalidCredentials(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                }
            }
            else -> SyntheticResponse.AuthenticationResult.ApiFailure(res.statusCode, res.jsonObject)
        }
    }
}
