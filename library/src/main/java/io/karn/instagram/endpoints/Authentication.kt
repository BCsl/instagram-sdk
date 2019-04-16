package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.AuthenticationAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.common.wrapAPIException
import io.karn.instagram.core.CookieUtils
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.exceptions.InstagramAPIException
import khttp.responses.Response
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

    fun authenticate(username: String, password: String, token: String? = null): SyntheticResponse.Auth {
        if (!token.isNullOrBlank()) {
            // Go straight to login.
            return processLogin(username, password, token)
        }

        val (res, error) = wrapAPIException { AuthenticationAPI.getTokenForAuth() }

        res ?: return SyntheticResponse.Auth.TokenFailure(error!!.statusCode, error.statusMessage)

        return when (res.statusCode) {
            200 -> {
                val newToken = AuthenticationAPI.parseCSRFToken(res).takeIf { !it.isNullOrBlank() || it != "null" }
                        ?: return SyntheticResponse.Auth.TokenFailure(412, res.text)

                processLogin(username, password, newToken)
            }
            else -> SyntheticResponse.Auth.TokenFailure(res.statusCode, res.text)
        }
    }

    fun twoFactorLogin(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): SyntheticResponse.TwoFactorResult {
        val data = Crypto.generateTwoFactorPayload(code.replace("\\s".toRegex(), ""), identifier, token, username, password, deviceId)

        val (res, error) = wrapAPIException { AuthenticationAPI.twoFactor(data) }

        res ?: return SyntheticResponse.TwoFactorResult.Failure(error!!)

        return when (res.statusCode) {
            200 -> SyntheticResponse.TwoFactorResult.Success(buildSuccess(res))
            else -> SyntheticResponse.TwoFactorResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
        }
    }

    fun prepareAuthChallenge(path: String): SyntheticResponse.ChallengeResult {
        val (res, error) = wrapAPIException { AuthenticationAPI.prepareAuthChallenge(path, Instagram.session) }

        res ?: return SyntheticResponse.ChallengeResult.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                Instagram.session.cookieJar = res.cookies

                if (res.jsonObject.optString("step_name") == "select_verify_method") {
                    SyntheticResponse.ChallengeResult.Success(res.jsonObject)
                } else {
                    SyntheticResponse.ChallengeResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
                }
            }
            else -> SyntheticResponse.ChallengeResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
        }
    }

    fun selectAuthChallengeMethod(path: String, method: String): SyntheticResponse.AuthMethodSelectionResult {
        val (res, error) = wrapAPIException { AuthenticationAPI.selectAuthChallengeMethod(path, method, Instagram.session) }

        res ?: return SyntheticResponse.AuthMethodSelectionResult.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                Instagram.session.cookieJar = res.cookies

                when (res.jsonObject.optString("step_name")) {
                    "verify_code" -> SyntheticResponse.AuthMethodSelectionResult.PhoneSelectionSuccess(res.jsonObject.optJSONObject("step_data")
                            ?: JSONObject())
                    "verify_email" -> SyntheticResponse.AuthMethodSelectionResult.EmailSelectionSuccess(res.jsonObject.optJSONObject("step_data")
                            ?: JSONObject())
                    else -> SyntheticResponse.AuthMethodSelectionResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
                }
            }
            else -> SyntheticResponse.AuthMethodSelectionResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
        }
    }

    fun submitChallengeCode(path: String, code: String): SyntheticResponse.ChallengeCodeSubmitResult {
        val (res, error) = wrapAPIException { AuthenticationAPI.submitAuthChallenge(path, code, Instagram.session) }

        res ?: return SyntheticResponse.ChallengeCodeSubmitResult.Failure(error!!)

        return when (res.statusCode) {
            200 -> {
                val token = AuthenticationAPI.parseCSRFToken(res).takeIf { !it.isNullOrBlank() || it != "null" }
                        ?: return SyntheticResponse.ChallengeCodeSubmitResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))

                SyntheticResponse.ChallengeCodeSubmitResult.Success(token)
            }
            else -> SyntheticResponse.ChallengeCodeSubmitResult.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
        }
    }

    fun logoutUser(): SyntheticResponse.Logout {
        val (res, error) = wrapAPIException { AuthenticationAPI.logout() }

        res ?: return SyntheticResponse.Logout.Failure(error!!)

        return when (res.statusCode) {
            200 -> SyntheticResponse.Logout.Success(res.statusCode)
            else -> SyntheticResponse.Logout.Failure(InstagramAPIException(res.statusCode, res.jsonObject.optString("message", Errors.ERROR_UNKNOWN)))
        }
    }

    private fun processLogin(username: String, password: String, token: String): SyntheticResponse.Auth {
        Instagram.session.uuid = Crypto.generateUUID(true)

        // Generate the login payload.
        val deviceId = Crypto.generateDeviceId(username, password)
        val data = Crypto.generateLoginPayload(token, username, password, 0, deviceId)

        val (res, error) = wrapAPIException { AuthenticationAPI.login(data) }

        res ?: return SyntheticResponse.Auth.Failure(error!!)

        return when (res.statusCode) {
            200 -> SyntheticResponse.Auth.Success(buildSuccess(res))
            400 -> {
                Instagram.session.cookieJar = res.cookies

                when {
                    res.jsonObject.optBoolean("two_factor_required") -> {
                        // User requires two factor.
                        SyntheticResponse.Auth.TwoFactorRequired(res.jsonObject
                                .put("token", token)
                                .put("device_id", deviceId))
                    }
                    res.jsonObject.has("challenge") -> {
                        // User needs to pass challenge
                        SyntheticResponse.Auth.ChallengeRequired(res.jsonObject.getJSONObject("challenge"))
                    }
                    else -> SyntheticResponse.Auth.InvalidCredentials(res.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                }
            }
            else -> SyntheticResponse.Auth.Failure(InstagramAPIException(res.statusCode, res.text))
        }
    }

    private fun buildSuccess(res: Response): JSONObject {
        Instagram.session.primaryKey = res.jsonObject.optJSONObject("logged_in_user").getString("pk")
        Instagram.session.cookieJar = res.cookies

        val auth = res.jsonObject
        auth.put("primaryKey", Instagram.session.primaryKey)
        auth.put("cookie", CookieUtils.serializeToJson(res.cookies))
        auth.put("uuid", Instagram.session.uuid)

        return auth
    }
}
