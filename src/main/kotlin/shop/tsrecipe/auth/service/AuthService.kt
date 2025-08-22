package shop.tsrecipe.auth.service

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import shop.tsrecipe.auth.api.AppPlatform
import shop.tsrecipe.auth.api.AuthResponse
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.api.TokenResponse
import shop.tsrecipe.auth.external.member.MemberResponse
import shop.tsrecipe.auth.external.member.MemberService
import shop.tsrecipe.auth.external.oauth.OAuthService
import shop.tsrecipe.auth.external.token.TokenService
import shop.tsrecipe.auth.external.token.TokenType

@Service
class AuthService(
    private val oauthService: OAuthService,
    private val memberService: MemberService,
    private val tokenService: TokenService
) {
    suspend fun signIn(platform: AppPlatform, command: SignInCommand): AuthResponse {
        val oauthId = getOAuthUserId(platform = platform, provider = command.oauthProvider, token = command.idToken)

        val response = AuthResponse(oauthId = oauthId)

        val member = getMember(provider = command.oauthProvider, oauthId = oauthId)

        if (member != null) {
            val tokens = issueTokensAndCaching(member.id)
            response.apply {
                tokenResponse = tokens.toResponse(member.id)
                isMember = true
            }
        }

        return response
    }

    private suspend fun getOAuthUserId(
        platform: AppPlatform,
        provider: OAuthProvider,
        token: String
    ): String {
        val decodedInfo = oauthService.decodeToken(platform, provider, token)
        return decodedInfo.subject
    }

    private suspend fun getMember(provider: OAuthProvider, oauthId: String): MemberResponse? {
        return memberService.getMemberByOAuthInfo(
            oauthProvider = provider,
            oauthId = oauthId
        )
    }

    private suspend fun issueTokensAndCaching(memberId: String): TokenResult {
        return coroutineScope {
            val accessTokenDeferred = async { tokenService.issue(memberId, TokenType.ACCESS) }
            val refreshTokenDeferred = async { tokenService.issue(memberId, TokenType.REFRESH) }

            val issuedTokens = TokenResult(
                accessTokenDeferred.await(),
                refreshTokenDeferred.await()
            )

            launch { tokenService.cachingRefreshToken(memberId, issuedTokens.refreshToken) }

            issuedTokens
        }
    }

    suspend fun logout(refreshToken: String) {
        tokenService.revokeRefreshTokenIfVerified(refreshToken)
    }

    suspend fun reissueToken(refreshToken: String): TokenResponse {
        val memberId = tokenService.revokeRefreshTokenIfVerified(refreshToken)

        val tokens = issueTokensAndCaching(memberId)

        return tokens.toResponse(memberId)
    }
}