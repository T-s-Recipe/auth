package shop.tsrecipe.auth.api

import io.swagger.v3.oas.annotations.media.Schema
import shop.tsrecipe.auth.service.SignInCommand

@Schema(description = "소셜 로그인 RequestDTO")
data class SignInRequest(
    @field:Schema(description = "ID Token")
    val idToken: String,

    @field:Schema(description = "플랫폼 구분 (GOOGLE, APPLE)")
    val provider: OAuthProvider,

    @field:Schema(description = "이름 (APPLE 최초 로그인 시 필수)")
    val name: String? = null,

    @field:Schema(description = "이메일 (APPLE 최초 로그인 시 필수)")
    val email: String? = null
) {
    fun toCommand(): SignInCommand {
        return SignInCommand(
            idToken = this.idToken,
            oAuthProvider = this.provider,
            name = this.name,
            email = this.email
        )
    }
}

enum class OAuthProvider {
    GOOGLE,
    APPLE
}

@Schema(description = "인증 토큰 ResponseDTO")
data class AuthTokenResponse(
    @field:Schema(description = "Access Token (1시간)")
    val accessToken: String,
    @field:Schema(description = "Refresh Token (30일)")
    val refreshToken: String
)

@Schema(description = "로그아웃 RequestDTO")
data class LogoutRequest(
    @field:Schema(description = "Refresh token")
    val refreshToken: String
)

@Schema(description = "토큰 재발급 RequestDTO")
data class TokenReissueRequest(
    @field:Schema(description = "Refresh token")
    val refreshToken: String
)