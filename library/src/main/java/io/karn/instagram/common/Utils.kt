package io.karn.instagram.common

import io.karn.instagram.exceptions.InstagramAPIException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Wraps an execution in a collection of Exception handlers which map the exception out to a single
 * {@link InstagramAPIException} object.
 */
internal fun <T> wrapAPIException(block: () -> T): Pair<T?, InstagramAPIException?> {
    val error = try {
        return Pair(block(), null)
    } catch (socketTimeout: SocketTimeoutException) {
        InstagramAPIException(408, "API request timed out.", socketTimeout)
    } catch (sslException: SSLException) {
        InstagramAPIException(408, "Unable to create connection", sslException)
    } catch (connectException: ConnectException) {
        InstagramAPIException(408, "Unable to create connection.", connectException)
    } catch (unknownHostException: UnknownHostException) {
        InstagramAPIException(408, "Unable to connect to host.", unknownHostException)
    }

    return Pair(null, error)
}

/**
 * Indicates the feature is in experimental state: its existence, signature or behavior
 * might change without warning from one release to the next.
 */
annotation class Experimental