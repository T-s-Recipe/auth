package shop.tsrecipe.auth.service

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.exception.BaseException
import shop.tsrecipe.auth.exception.ErrorCode
import shop.tsrecipe.auth.external.member.MemberResponse
import shop.tsrecipe.auth.external.member.MemberService
import shop.tsrecipe.auth.external.oauth.OAuthService
import shop.tsrecipe.auth.external.oauth.OAuthUserInfo
import shop.tsrecipe.auth.external.token.TokenService
import shop.tsrecipe.auth.external.token.TokenType

@Service
class AuthService(
    private val oauthService: OAuthService,
    private val memberService: MemberService,
    private val tokenService: TokenService
) {
    suspend fun signIn(command: SignInCommand): TokenResult {
        val oauthUserInfo = getVerifiedOAuthUserInfo(
            provider = command.oauthProvider, token = command.idToken
        )

        val member = getMemberOrThrow(provider = command.oauthProvider, oauthId = oauthUserInfo.id)

        return issueTokensAndCaching(member.id)
    }

    private suspend fun getVerifiedOAuthUserInfo(provider: OAuthProvider, token: String): OAuthUserInfo {
        val userInfo = oauthService.getUserInfoByToken(
            provider = provider,
            decodedJWT = oauthService.decodeToken(provider, token)
        )

        if (!userInfo.emailVerified) {
            throw BaseException(ErrorCode.NOT_VERIFIED_USER)
        }

        return userInfo
    }

    private suspend fun getMemberOrThrow(provider: OAuthProvider, oauthId: String): MemberResponse {
        return memberService.getMemberByOAuthInfo(
            oauthProvider = provider,
            oauthId = oauthId
        ) ?: throw BaseException(ErrorCode.MEMBER_NOT_FOUND)
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

    suspend fun reissueToken(refreshToken: String): TokenResult {
        val memberId = tokenService.revokeRefreshTokenIfVerified(refreshToken)

        return issueTokensAndCaching(memberId)
    }
}