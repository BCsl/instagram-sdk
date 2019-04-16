package io.karn.instagram.exceptions

class InstagramSDKException : Exception {

    internal constructor(message: String) : super(message)

    internal constructor(message: String, cause: Throwable) : super(message, cause)
}