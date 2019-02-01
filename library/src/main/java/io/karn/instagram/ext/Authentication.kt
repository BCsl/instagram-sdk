package io.karn.instagram.ext

import io.karn.instagram.common.Errors
import io.karn.instagram.core.SyntheticResponse
import io.karn.instagram.endpoints.Authentication
import io.reactivex.Single

fun Authentication.authenticate(username: String, password: String, token: String? = null): Single<SyntheticResponse.AuthenticationResult> {
    return Single.create<SyntheticResponse.AuthenticationResult> { it.onSuccess(this.authenticate(username, password, token)) }
            .onErrorReturn { SyntheticResponse.AuthenticationResult.Error(it) }
}

fun Authentication.twoFactorLogin(code: String, identifier: String, token: String, deviceId: String, username: String, password: String): Single<SyntheticResponse.TwoFactorAuthResult> {
    return Single.create<SyntheticResponse.TwoFactorAuthResult> { it.onSuccess(this.twoFactorLogin(code, identifier, token, deviceId, username, password)) }
            .onErrorReturn { SyntheticResponse.TwoFactorAuthResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.prepareAuthChallenge(path: String): Single<SyntheticResponse.AuthChallengeResult> {
    return Single.create<SyntheticResponse.AuthChallengeResult> { it.onSuccess(this.prepareAuthChallenge(path)) }
            .onErrorReturn { SyntheticResponse.AuthChallengeResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.selectAuthChallengeMethod(path: String, method: String): Single<SyntheticResponse.AuthMethodSelectedResult> {
    return Single.create<SyntheticResponse.AuthMethodSelectedResult> { it.onSuccess(this.selectAuthChallengeMethod(path, method)) }
            .onErrorReturn { SyntheticResponse.AuthMethodSelectedResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.submitChallengeCode(path: String, code: String): Single<SyntheticResponse.ChallengeCodeSubmitResult> {
    return Single.create<SyntheticResponse.ChallengeCodeSubmitResult> { it.onSuccess(this.submitChallengeCode(path, code)) }
            .onErrorReturn { SyntheticResponse.ChallengeCodeSubmitResult.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}

fun Authentication.logoutUser(): Single<SyntheticResponse> {
    return Single.create<SyntheticResponse> { it.onSuccess(this.logoutUser()) }
            .onErrorReturn { SyntheticResponse.Failure(it.message ?: Errors.ERROR_UNKNOWN) }
}