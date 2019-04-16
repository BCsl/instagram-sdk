package io.karn.instagram.core

import io.karn.instagram.exceptions.InstagramAPIException
import org.json.JSONArray
import org.json.JSONObject

sealed class SyntheticResponse {

    sealed class Auth : SyntheticResponse() {
        data class Success(val data: JSONObject) : Auth()

        data class TwoFactorRequired(val data: JSONObject) : Auth()

        data class ChallengeRequired(val data: JSONObject) : Auth()

        data class InvalidCredentials(val message: String) : Auth()

        data class TokenFailure(val statusCode: Int, val message: String) : Auth()

        data class Failure(val exception: InstagramAPIException) : Auth()
    }

    sealed class TwoFactorResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : TwoFactorResult()

        data class Failure(val exception: InstagramAPIException) : TwoFactorResult()
    }

    sealed class ChallengeResult : SyntheticResponse() {
        data class Success(val data: JSONObject) : ChallengeResult()

        data class Failure(val exception: InstagramAPIException) : ChallengeResult()
    }

    sealed class AuthMethodSelectionResult : SyntheticResponse() {
        data class PhoneSelectionSuccess(val data: JSONObject) : AuthMethodSelectionResult()

        data class EmailSelectionSuccess(val data: JSONObject) : AuthMethodSelectionResult()

        data class Failure(val exception: InstagramAPIException) : AuthMethodSelectionResult()
    }

    sealed class ChallengeCodeSubmitResult : SyntheticResponse() {
        data class Success(val token: String) : ChallengeCodeSubmitResult()

        data class Failure(val exception: InstagramAPIException) : ChallengeCodeSubmitResult()
    }

    sealed class AccountDetails : SyntheticResponse() {
        data class Success(val details: JSONObject) : AccountDetails()

        data class Failure(val exception: InstagramAPIException) : AccountDetails()
    }

    sealed class Relationships : SyntheticResponse() {
        data class Success(val nextMaxId: String, val relationships: JSONArray) : Relationships()

        data class Failure(val exception: InstagramAPIException) : Relationships()
    }

    sealed class RelationshipUpdate : SyntheticResponse() {
        data class Success(val friendshipStatus: JSONObject) : RelationshipUpdate()

        data class Failure(val exception: InstagramAPIException) : RelationshipUpdate()
    }

    sealed class Blocks : SyntheticResponse() {
        data class Success(val profiles: JSONArray) : Blocks()

        data class Failure(val exception: InstagramAPIException) : Blocks()
    }

    sealed class ProfileSearch : SyntheticResponse() {
        data class Success(val profiles: JSONArray) : ProfileSearch()

        data class Failure(val exception: InstagramAPIException) : ProfileSearch()
    }

    sealed class StoryReel : SyntheticResponse() {
        data class Success(val stories: JSONArray) : StoryReel()

        data class Failure(val exception: InstagramAPIException) : StoryReel()
    }

    sealed class ProfileFeed : SyntheticResponse() {
        data class Success(val nextMaxId: String, val feed: JSONArray) : ProfileFeed()

        data class Failure(val exception: InstagramAPIException) : ProfileFeed()
    }

    sealed class MediaLikes : SyntheticResponse() {
        data class Success(val likes: JSONArray) : MediaLikes()

        data class Failure(val exception: InstagramAPIException) : MediaLikes()
    }

    sealed class MediaComments : SyntheticResponse() {
        data class Success(val comments: JSONArray) : MediaComments()

        data class Failure(val exception: InstagramAPIException) : MediaComments()
    }

    sealed class DirectMessages : SyntheticResponse() {
        data class Success(val unseenCount: Int, val threads: JSONArray) : DirectMessages()

        data class Failure(val exception: InstagramAPIException) : DirectMessages()
    }

    sealed class Logout : SyntheticResponse() {
        data class Success(val statusCode: Int) : Logout()

        data class Failure(val exception: InstagramAPIException) : Logout()
    }
}
