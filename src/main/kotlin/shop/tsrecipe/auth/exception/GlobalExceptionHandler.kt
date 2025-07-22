package shop.tsrecipe.auth.exception

import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import shop.tsrecipe.auth.util.Logging
import shop.tsrecipe.auth.util.baseResponse

@RestControllerAdvice
class GlobalExceptionHandler: Logging {
    @ExceptionHandler(Exception::class)
    fun handleUndefinedException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error { """
            Undefined exception.
            $e
        """.trimIndent() }
        return baseResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            body = ErrorResponse(ErrorCode.UNDEFINED_EXCEPTION)
        )
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(e: BaseException): ResponseEntity<ErrorResponse> {
        return baseResponse(e.httpStatus, ErrorResponse(e))
    }

    @ExceptionHandler(JWTVerificationException::class)
    fun handleJwtException(e: JWTVerificationException): ResponseEntity<ErrorResponse> {
        val errorCode = when(e) {
            is TokenExpiredException -> ErrorCode.TOKEN_EXPIRED
            else -> ErrorCode.INVALID_TOKEN
        }

        return baseResponse(
            status = errorCode.status,
            body = ErrorResponse(errorCode)
        )
    }
}