package io.karn.instagram.exceptions

import org.json.JSONObject

/**
 * Thrown when the Instagram API returns a non-200 status code. The Status code and error message are converted into
 * JSON object with the following structure:
 *  {
 *      'status_code': 500,
 *      'message': 'Internal Server Error'
 *  }
 *
 * This exception may also wrap a different exception.
 */
class InstagramAPIException : Exception {

    internal constructor(statusCode: Int, message: String) : super(buildMessage(statusCode, message))

    internal constructor(statusCode: Int, message: String, cause: Throwable) : super(buildMessage(statusCode, message), cause)

    companion object {
        private fun buildMessage(statusCode: Int, message: String) =
                JSONObject().also {
                    it.put("status_code", statusCode)
                    it.put("message", message)
                }.toString()
    }
}