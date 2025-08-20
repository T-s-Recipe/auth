package shop.tsrecipe.auth.api

import io.swagger.v3.oas.annotations.media.Schema
import shop.tsrecipe.auth.service.SignInCommand

@Schema(description = "소셜 로그인 RequestDTO")
data class SignInRequest(
    @field:Schema(description = "ID Token")
    val idToken: String,

    @field:Schema(description = "플랫폼 구분 (GOOGLE, APPLE)")
    val provider: OAuthProvider
) {
    fun toCommand(): SignInCommand {
        return SignInCommand(
            idToken = this.idToken,
            oauthProvider = this.provider
        )
    }
}

enum class OAuthProvider {
    GOOGLE,
    APPLE
}

enum class AppPlatform {
    ios,
    android
}

@Schema(description = "인증 ResponseDTO")
data class AuthResponse(
    @field:Schema(description = "인증 토큰 ResponseDTO")
    var tokenResponse: TokenResponse? = null,

    @field:Schema(description = "OAuth ID")
    val oauthId: String,

    @field:Schema(description = "기존 회원 여부")
    var isMember: Boolean = false,
)

@Schema(description = "로그아웃 RequestDTO")
data class LogoutRequest(
    @field:Schema(description = "Refresh token")
    val refreshToken: String
)

@Schema(description = "인증 토큰 ResponseDTO")
data class TokenResponse(
    @field:Schema(description = "Access Token (1시간)")
    val accessToken: String,

    @field:Schema(description = "Refresh Token (30일)")
    val refreshToken: String,

    @field:Schema(description = "Member ID")
    var memberId: String
)