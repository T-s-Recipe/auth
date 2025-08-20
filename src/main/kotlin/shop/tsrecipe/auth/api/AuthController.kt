package shop.tsrecipe.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import shop.tsrecipe.auth.service.AuthService
import shop.tsrecipe.auth.util.baseResponse

@Tag(name = "Auth", description = "Auth APIs")
@RestController
class AuthController(
    private val authService: AuthService
) {
    @Operation(
        summary = "Sign in",
        description = "사용자 로그인"
    )
    @PostMapping("/sign-in")
    suspend fun signIn(
        @RequestBody request: SignInRequest,
        @RequestHeader(HeaderNames.APP_PLATFORM) appPlatform: AppPlatform
    ): ResponseEntity<AuthResponse> {
        return baseResponse(
            body = authService.signIn(appPlatform, request.toCommand())
        )
    }

    @Operation(
        summary = "Logout",
        description = "사용자 로그아웃"
    )
    @PostMapping("/logout")
    suspend fun logout(@RequestBody request: LogoutRequest): ResponseEntity<Void> {
        authService.logout(request.refreshToken)
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "토큰 재발급",
        description = "토큰 재발급 (Access + Refresh)"
    )
    @PostMapping("/reissue")
    suspend fun reissueToken(@RequestParam refreshToken: String): ResponseEntity<TokenResponse> {
        return baseResponse(
            body = authService.reissueToken(refreshToken)
        )
    }
}