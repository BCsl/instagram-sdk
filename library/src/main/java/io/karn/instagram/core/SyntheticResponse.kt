package io.karn.instagram.core

import org.json.JSONArray
import org.json.JSONObject

sealed class SyntheticResponse {

    sealed class AuthenticationResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : AuthenticationResult()

        data class TwoFactorAuth(val data: JSONObject) : AuthenticationResult()

        data class AuthChallenge(val data: JSONObject) : AuthenticationResult()

        data class TokenFailure(val statusCode: Int, val data: JSONObject) : AuthenticationResult()

        data class ApiFailure(val statusCode: Int, val data: JSONObject) : AuthenticationResult()

        data class InvalidCredentials(val message: String) : AuthenticationResult()

        data class Failure(val message: String) : AuthenticationResult()

        data class Error(val error: Throwable) : AuthenticationResult()
    }

    sealed class TwoFactorAuthResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : TwoFactorAuthResult()

        data class Failure(val message: String) : TwoFactorAuthResult()
    }

    sealed class AuthChallengeResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : AuthChallengeResult()

        data class Failure(val message: String) : AuthChallengeResult()
    }

    sealed class AuthMethodSelectedResult : SyntheticResponse() {
        data class PhoneSelectionSuccess(val data: JSONObject) : AuthMethodSelectedResult()

        data class EmailSelectionSuccess(val data: JSONObject) : AuthMethodSelectedResult()

        data class Failure(val message: String) : AuthMethodSelectedResult()
    }

    sealed class ChallengeCodeSubmitResult : SyntheticResponse() {
        data class Success(val token: String) : ChallengeCodeSubmitResult()

        data class Failure(val message: String) : ChallengeCodeSubmitResult()
    }

    sealed class AccountDetails : SyntheticResponse() {
        data class Success(val details: JSONObject) : AccountDetails()

        data class Failure(val message: String) : AccountDetails()
    }

    sealed class Relationships : SyntheticResponse() {
        data class Success(val nextMaxId: String, val relationships: JSONArray) : Relationships()

        data class Failure(val message: String) : Relationships()
    }

    sealed class ProfileSearch : SyntheticResponse() {
        data class Success(val profiles: JSONArray) : ProfileSearch()

        data class Failure(val message: String) : ProfileSearch()
    }

    sealed class StoryReel : SyntheticResponse() {
        data class Success(val stories: JSONArray) : StoryReel()

        data class Failure(val message: String) : StoryReel()
    }

    sealed class ProfileFeed : SyntheticResponse() {
        data class Success(val feed: JSONArray) : ProfileFeed()

        data class Failure(val message: String) : ProfileFeed()
    }

    sealed class Logout : SyntheticResponse() {
        data class Success(val statusCode: Int) : Logout()

        data class Failure(val message: String) : Logout()
    }
}
