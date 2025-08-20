package shop.tsrecipe.auth.service

import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.api.TokenResponse

data class SignInCommand(
    val idToken: String,
    val oauthProvider: OAuthProvider
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)

fun TokenResult.toResponse(memberId: String): TokenResponse {
    return TokenResponse(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        memberId = memberId
    )
}