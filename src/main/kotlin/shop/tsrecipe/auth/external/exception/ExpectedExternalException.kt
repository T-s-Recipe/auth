package shop.tsrecipe.auth.external.exception

import org.springframework.http.HttpStatus
import shop.tsrecipe.auth.external.exception.ExpectedExternalException.*

sealed class ExpectedExternalException : RuntimeException() {
    data object MemberNotFoundException : ExpectedExternalException() {
        private fun readResolve(): Any = MemberNotFoundException
    }

    data object UndefinedException : ExpectedExternalException() {
        private fun readResolve(): Any = UndefinedException
    }
}

enum class ExpectedError(
    val httpStatus: HttpStatus,
    val code: String,
    val exception: ExpectedExternalException
) {
    MEMBER_NOT_FOUND(
        httpStatus = HttpStatus.NOT_FOUND,
        code = "MEMBER_NOT_FOUND",
        exception = MemberNotFoundException
    ),
    UNDEFINED_ERROR(
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        code = "UNDEFINED_ERROR",
        exception = UndefinedException
    )
    ;

    companion object {
        operator fun invoke(httpStatus: HttpStatus, code: String?): ExpectedError {
            return entries.find { httpStatus == it.httpStatus && code == it.code } ?: UNDEFINED_ERROR
        }
    }
}