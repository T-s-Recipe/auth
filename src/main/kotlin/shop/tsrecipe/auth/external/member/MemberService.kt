package shop.tsrecipe.auth.external.member

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.exception.BaseException
import shop.tsrecipe.auth.exception.ErrorCode
import shop.tsrecipe.auth.external.exception.ExpectedError
import shop.tsrecipe.auth.external.exception.ExpectedExternalException.MemberNotFoundException
import shop.tsrecipe.auth.external.exception.ExternalServiceException
import shop.tsrecipe.auth.external.exception.getLogMessage
import shop.tsrecipe.auth.util.Logging

@Service
class MemberService(
    private val memberClient: MemberClient
) : Logging {

    @CircuitBreaker(name = "member-service", fallbackMethod = "getMemberFailed")
    suspend fun getMemberByOAuthInfo(oAuthProvider: OAuthProvider, oAuthId: String): MemberResponse? {
        return try {
            memberClient.getMember(
                memberId = null,
                oAuthProvider = oAuthProvider,
                oAuthId = oAuthId
            ).awaitSingle()
        } catch (e: BaseException) {
            val expectedException = ExpectedError(httpStatus = e.httpStatus, code = e.code)
            if (expectedException.exception == MemberNotFoundException) {
                return null
            } else {
                throw e
            }
        }
    }

    private suspend fun getMemberFailed(throwable: Throwable) {
        val defaultMessage = "[fallback method]: getMemberFailed.\n"
        when (throwable) {
            is ExternalServiceException -> {
                logger.error { "$defaultMessage ${throwable.getLogMessage()}" }
                throw BaseException(throwable)
            }

            else -> {
                logger.error { "$defaultMessage undefined error." }
                throw BaseException(ErrorCode.UNDEFINED_EXCEPTION)
            }
        }
    }
}