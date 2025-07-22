package shop.tsrecipe.auth.service

import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.api.AuthTokenResponse

data class SignInCommand(
    val idToken: String,
    val oAuthProvider: OAuthProvider,
    val name: String?,
    val email: String?
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)

fun TokenResult.toResponse(): AuthTokenResponse {
    return AuthTokenResponse(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )
}