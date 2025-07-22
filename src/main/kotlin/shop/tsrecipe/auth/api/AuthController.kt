package shop.tsrecipe.auth.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import shop.tsrecipe.auth.service.AuthService
import shop.tsrecipe.auth.service.toResponse
import shop.tsrecipe.auth.util.baseResponse

@RestController
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/sign-in")
    suspend fun signIn(@RequestBody request: SignInRequest): ResponseEntity<AuthTokenResponse> {
        return baseResponse(
            body = authService.signIn(request.toCommand()).toResponse()
        )
    }

    @PostMapping("/logout")
    suspend fun logout(@RequestBody request: LogoutRequest) {
        authService.logout(request.refreshToken)
    }

    @PostMapping("/reissue")
    suspend fun reissueToken(@RequestBody request: TokenReissueRequest): ResponseEntity<AuthTokenResponse> {
        return baseResponse(
            body = authService.reissueToken(request.refreshToken).toResponse()
        )
    }
}