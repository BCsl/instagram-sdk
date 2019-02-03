package io.karn.instagram.exceptions

/**
 * Used when the SDK suffers an unrecoverable error which should not be caught.
 */
class InstagramSDKError internal constructor(message: String) : Error(message)