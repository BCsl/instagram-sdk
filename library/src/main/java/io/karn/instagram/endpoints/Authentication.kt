package io.karn.instagram.endpoints

import io.karn.instagram.Instagram
import io.karn.instagram.api.AuthenticationAPI
import io.karn.instagram.common.Errors
import io.karn.instagram.core.CookieUtils
import io.karn.instagram.core.Crypto
import io.karn.instagram.core.SyntheticResponse
import khttp.responses.Response
import org.json.JSONObject

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

        return try {
            val token = AuthenticationAPI.getTokenForAuth()

            processLogin(username, password, token)
        } catch (ex: Exception) {
            SyntheticResponse.AuthenticationResult.Failure(ex.message ?: Errors.ERROR_INVALID_CSRF)
        }
    }

    fun twoFactorLogin(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): SyntheticResponse.TwoFactorAuthResult {
        val data = Crypto.generateTwoFactorPayload(code.replace("\\s".toRegex(), ""), identifier, token, username, password, deviceId)

        return AuthenticationAPI.twoFactor(data)
                .let { response: Response ->
                    return@let when (response.statusCode) {
                        200 -> {
                            val auth = response.jsonObject
                            auth.put("cookie", CookieUtils.serializeToJson(response.cookies))
                            auth.put("uuid", Instagram.session.uuid)

                            SyntheticResponse.TwoFactorAuthResult.Success(auth)
                        }
                        else -> SyntheticResponse.TwoFactorAuthResult.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                    }
                }
    }

    fun prepareAuthChallenge(path: String): SyntheticResponse.AuthChallengeResult =
            AuthenticationAPI.prepareAuthChallenge(path, Instagram.session)
                    .let {
                        return@let when (it.statusCode) {
                            200 -> {
                                Instagram.session.cookieJar = it.cookies

                                if (it.jsonObject.optString("step_name") == "select_verify_method") {
                                    SyntheticResponse.AuthChallengeResult.Success(it.jsonObject)
                                } else {
                                    SyntheticResponse.AuthChallengeResult.Failure(it.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                                }
                            }
                            else -> SyntheticResponse.AuthChallengeResult.Failure(it.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                        }
                    }

    fun selectAuthChallengeMethod(path: String, method: String): SyntheticResponse.AuthMethodSelectedResult =
            AuthenticationAPI.selectAuthChallengeMethod(path, method, Instagram.session)
                    .let { response: Response ->
                        return@let when (response.statusCode) {
                            200 -> {
                                Instagram.session.cookieJar = response.cookies

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

    fun submitChallengeCode(path: String, code: String): SyntheticResponse.ChallengeCodeSubmitResult {
        return try {
            val token = AuthenticationAPI.submitAuthChallenge(path, code, Instagram.session)

            SyntheticResponse.ChallengeCodeSubmitResult.Success(token)
        } catch (ex: Exception) {
            SyntheticResponse.ChallengeCodeSubmitResult.Failure(ex.message ?: Errors.ERROR_INVALID_CSRF)
        }
    }

    fun logoutUser(): SyntheticResponse.Logout =
            AuthenticationAPI.logout()
                    .let { response: Response ->
                        return@let when (response.statusCode) {
                            200 -> SyntheticResponse.Logout.Success(response.statusCode)
                            else -> SyntheticResponse.Logout.Failure(response.jsonObject.optString("message", Errors.ERROR_UNKNOWN))
                        }
                    }

    private fun processLogin(username: String, password: String, token: String): SyntheticResponse.AuthenticationResult {
        Instagram.session.uuid = Crypto.generateUUID(true)

        // Generate the login payload.
        val deviceId = Crypto.generateDeviceId(username, password)
        val data = Crypto.generateLoginPayload(token, username, password, 0, deviceId)

        return AuthenticationAPI.login(data)
                .let {
                    return@let when (it.statusCode) {
                        200 -> {
                            Instagram.session.cookieJar = it.cookies

                            val auth = it.jsonObject

                            auth.put("cookie", CookieUtils.serializeToJson(it.cookies))
                            auth.put("uuid", Instagram.session.uuid)

                            SyntheticResponse.AuthenticationResult.Success(auth)
                        }
                        400 -> {
                            Instagram.session.cookieJar = it.cookies

                            val result = when {
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
}
