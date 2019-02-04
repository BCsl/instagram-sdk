package io.karn.instagram.core

import org.json.JSONArray
import org.json.JSONObject

sealed class SyntheticResponse {

    sealed class Auth : SyntheticResponse() {
        data class Success(val data: JSONObject) : Auth()

        data class TwoFactorRequired(val data: JSONObject) : Auth()

        data class ChallengeRequired(val data: JSONObject) : Auth()

        data class InvalidCredentials(val message: String) : Auth()

        data class TokenFailure(val statusCode: Int, val message: String) : Auth()

        data class Failure(val statusCode: Int, val message: String) : Auth()
    }

    sealed class TwoFactorResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : TwoFactorResult()

        data class Failure(val statusCode: Int, val message: String) : TwoFactorResult()
    }

    sealed class ChallengeResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : ChallengeResult()

        data class Failure(val statusCode: Int, val message: String) : ChallengeResult()
    }

    sealed class AuthMethodSelectionResult : SyntheticResponse() {
        data class PhoneSelectionSuccess(val data: JSONObject) : AuthMethodSelectionResult()

        data class EmailSelectionSuccess(val data: JSONObject) : AuthMethodSelectionResult()

        data class Failure(val statusCode: Int, val message: String) : AuthMethodSelectionResult()
    }

    sealed class ChallengeCodeSubmitResult : SyntheticResponse() {
        data class Success(val token: String) : ChallengeCodeSubmitResult()

        data class Failure(val statusCode: Int, val message: String) : ChallengeCodeSubmitResult()
    }

    sealed class AccountDetails : SyntheticResponse() {
        data class Success(val details: JSONObject) : AccountDetails()

        data class Failure(val statusCode: Int, val message: String) : AccountDetails()
    }

    sealed class Relationships : SyntheticResponse() {
        data class Success(val nextMaxId: String, val relationships: JSONArray) : Relationships()

        data class Failure(val statusCode: Int, val message: String) : Relationships()
    }

    sealed class ProfileSearch : SyntheticResponse() {
        data class Success(val profiles: JSONArray) : ProfileSearch()

        data class Failure(val statusCode: Int, val message: String) : ProfileSearch()
    }

    sealed class StoryReel : SyntheticResponse() {
        data class Success(val stories: JSONArray) : StoryReel()

        data class Failure(val statusCode: Int, val message: String) : StoryReel()
    }

    sealed class ProfileFeed : SyntheticResponse() {
        data class Success(val feed: JSONArray) : ProfileFeed()

        data class Failure(val statusCode: Int, val message: String) : ProfileFeed()
    }

    sealed class Logout : SyntheticResponse() {
        data class Success(val statusCode: Int) : Logout()

        data class Failure(val statusCode: Int, val message: String) : Logout()
    }
}
