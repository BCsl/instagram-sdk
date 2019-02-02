package io.karn.instagram.common

object Errors {
    const val ERROR_UNKNOWN = "An unknown error occurred."
    internal const val ERROR_INVALID_CSRF = "Unable to fetch authentication token."
    internal const val ERROR_ACCOUNT_FETCH = "Unable to fetch account details. Error code: %s Message: %s"
    internal const val ERROR_FEED_FETCH = "Unable to fetch feed. Error code: %s Message: %s"

    internal const val ERROR_SEARCH_NO_RESULTS = "Search completed with 0 results."
    internal const val ERROR_INCOMPLETE_SEARCH = "Cannot complete search. Error code: %s Message: %s"
    internal const val ERROR_STORIES_FAILED = "Cannot fetch stories. Error code: %s Message: %s"

    class TokenFetchException(message: String, throwable: Throwable? = null) : Exception(message, throwable)
    class AuthenticationException(message: String) : Exception(message)
}
