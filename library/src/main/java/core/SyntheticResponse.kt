package core

import org.json.JSONArray
import org.json.JSONObject

sealed class SyntheticResponse {

    data class Success<out T>(val data: T) : SyntheticResponse()

    data class Failure(val message: String) : SyntheticResponse()

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

    sealed class RelationshipFetchResult : SyntheticResponse() {
        data class Success(val nextMaxId: String, val relationships: JSONArray) : RelationshipFetchResult()

        data class Failure(val message: String) : RelationshipFetchResult()
    }

    sealed class ProfileSearchResult : SyntheticResponse() {
        data class Success(val profiles: JSONArray) : ProfileSearchResult()

        data class Failure(val message: String) : ProfileSearchResult()
    }

    sealed class StoryReelResult : SyntheticResponse() {
        data class Success(val stories: JSONArray) : StoryReelResult()

        data class Failure(val message: String) : StoryReelResult()
    }

    sealed class ProfileFeedResult : SyntheticResponse() {
        data class Success(val feed: JSONArray) : ProfileFeedResult()

        data class Failure(val message: String) : ProfileFeedResult()
    }
}
