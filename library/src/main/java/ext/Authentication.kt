package ext

import common.Errors
import core.SyntheticResponse
import endpoints.Authentication
import io.reactivex.Single

fun Authentication.authenticateSingle(username: String, password: String, token: String? = null): Single<SyntheticResponse.AuthenticationResult> {
    return Single.create<SyntheticResponse.AuthenticationResult> { it.onSuccess(this.authenticate(username, password, token)) }
            .onErrorReturn { SyntheticResponse.AuthenticationResult.Error(it) }
}

fun Authentication.twoFactorLoginSingle(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): Single<SyntheticResponse.TwoFactorAuthResult> {
    return Single.create<SyntheticResponse.TwoFactorAuthResult> { it.onSuccess(this.twoFactorLogin(code, identifier, token, deviceId, username, password)) }
            .onErrorReturn { SyntheticResponse.TwoFactorAuthResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.prepareAuthChallengeSingle(path: String): Single<SyntheticResponse.AuthChallengeResult> {
    return Single.create<SyntheticResponse.AuthChallengeResult> { it.onSuccess(this.prepareAuthChallenge(path)) }
            .onErrorReturn { SyntheticResponse.AuthChallengeResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.selectAuthChallengeMethodSingle(path: String, method: String): Single<SyntheticResponse.AuthMethodSelectedResult> {
    return Single.create<SyntheticResponse.AuthMethodSelectedResult> { it.onSuccess(this.selectAuthChallengeMethod(path, method)) }
            .onErrorReturn { SyntheticResponse.AuthMethodSelectedResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.submitChallengeCodeSingle(path: String, code: String): Single<SyntheticResponse.ChallengeCodeSubmitResult> {
    return Single.create<SyntheticResponse.ChallengeCodeSubmitResult> { it.onSuccess(this.submitChallengeCode(path, code)) }
            .onErrorReturn { SyntheticResponse.ChallengeCodeSubmitResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.logoutUserSingle(): Single<SyntheticResponse> {
    return Single.create<SyntheticResponse> { it.onSuccess(this.logoutUser()) }
            .onErrorReturn { SyntheticResponse.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}