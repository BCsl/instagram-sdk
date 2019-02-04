package io.karn.instagram.core

import io.karn.instagram.Instagram
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.HashMap
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal object Crypto {
    private const val SIG_KEY = "673581b0ddb792bf47da5f9ca816b613d7996f342723aa06993a3f0552311c7d"
    const val SIG_VERSION = "4"

    private const val APP_VERSION = "42.0.0.19.95"

    private const val DEVICE_MANUFACTURER: String = "samsung"
    private const val DEVICE_DEVICE: String = "herolte"
    private const val DEVICE_MODEL: String = "SM-G930F"
    internal const val DPI: String = "640dpi"
    internal const val DISPLAY_RESOLUTION: String = "1440x2560"
    private const val DEVICE_ANDROID_VERSION: String = "24"
    private const val DEVICE_ANDROID_RELEASE: String = "7.0"
    private const val CHIPSET: String = "samsungexynos8890"
    private const val VERSION_CODE: String = "104766893"

    private const val USER_AGENT = "Instagram $APP_VERSION Android ($DEVICE_ANDROID_VERSION/$DEVICE_ANDROID_RELEASE; $DPI; $DISPLAY_RESOLUTION; $DEVICE_MANUFACTURER; $DEVICE_DEVICE; $DEVICE_MODEL; $CHIPSET; en_US; $VERSION_CODE)"

    val HEADERS: HashMap<String, String> = hashMapOf(
            "Accept-Encoding" to "gzip, deflate",
            "Connection" to "close",
            "Accept" to "*/*",
            "Content-Type" to "application/x-www-form-urlencoded; charset=UTF-8",
            "Cookie2" to "\$Version=1",
            "Accept-Language" to "en-US",
            "User-Agent" to buildUserAgent()
    )

    fun buildUserAgent(androidVersion: Int = android.os.Build.VERSION.SDK_INT,
                       androidRelease: String = android.os.Build.VERSION.RELEASE,
                       dpi: String = Instagram.getInstance().configuration.deviceDPI,
                       resolution: String = Instagram.getInstance().configuration.deviceResolution,
                       manufacturer: String = android.os.Build.MANUFACTURER,
                       device: String = android.os.Build.DEVICE,
                       model: String = android.os.Build.MODEL,
                       chipset: String = android.os.Build.BOARD): String {

        if (!Instagram.getInstance().configuration.deviceUA) {
            return USER_AGENT
        }

        return "Instagram $APP_VERSION Android ($androidVersion/$androidRelease; $dpi; $resolution; $manufacturer; $device; $model; $chipset; en_US; $VERSION_CODE)"
    }

    fun generateUUID(dash: Boolean): String {
        val uuid = UUID.randomUUID().toString()

        return if (dash) {
            uuid
        } else uuid.replace("-", "")

    }

    fun generateLoginPayload(token: String, username: String, password: String, loginAttempts: Int, deviceId: String = generateDeviceId(username, password)): String {
        val data = JSONObject()
                .put("phone_id", generateUUID(true))
                .put("_csrftoken", token)
                .put("username", username)
                .put("guid", Instagram.session.uuid)
                .put("device_id", deviceId)
                .put("password", password)
                .put("login_attempt_count", loginAttempts)

        return generateSignature(data.toString())
    }

    fun generateTwoFactorPayload(code: String, identifier: String, token: String, username: String, password: String, deviceId: String = generateDeviceId(username, password)): String {
        val data = JSONObject()
                .put("verification_code", code)
                .put("two_factor_identifier", identifier)
                .put("_csrftoken", token)
                .put("username", username)
                .put("device_id", deviceId)
                .put("password", password)

        return generateSignature(data.toString())
    }

    private fun digest(codec: String, source: String): String {
        val digest = MessageDigest.getInstance(codec)
        val digestBytes = digest.digest(source.toByteArray(Charset.forName("UTF-8")))

        return bytesToHex(digestBytes)
    }

    private fun md5Hex(source: String): String = digest("MD5", source)

    fun generateDeviceId(username: String, password: String): String {
        val seed = md5Hex(username + password)
        val volatileSeed = "12345"

        return "android-" + md5Hex(seed + volatileSeed).substring(0, 16)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val builder = StringBuilder()

        bytes.forEach { builder.append(String.format("%02x", it)) }

        return builder.toString()
    }

    private fun generateSignedBody(key: String, data: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(Charset.forName("UTF-8")), "HmacSHA256")
        sha256HMAC.init(secretKey)

        return bytesToHex(sha256HMAC.doFinal(data.toByteArray(Charset.forName("UTF-8")))).toLowerCase()
    }

    private fun generateSignature(payload: String): String {
        val parsedData = URLEncoder.encode(payload, "UTF-8")

        val signedBody = generateSignedBody(SIG_KEY, payload)

        return ("ig_sig_key_version=$SIG_VERSION&signed_body=$signedBody.$parsedData")
    }
}