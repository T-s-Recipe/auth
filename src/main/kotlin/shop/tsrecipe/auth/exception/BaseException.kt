package shop.tsrecipe.auth.exception

import org.springframework.http.HttpStatus
import shop.tsrecipe.auth.external.exception.ExternalServiceException
import shop.tsrecipe.auth.util.getCurrentTimestamp

class BaseException(
    val httpStatus: HttpStatus,
    val code: String? = null,
    override val message: String
) : RuntimeException() {

    val timestamp = getCurrentTimestamp()

    constructor(e: ErrorCode) : this(
        httpStatus = e.status!!,
        code = e.name,
        message = e.message,
    )

    constructor(e: ExternalServiceException) : this(
        httpStatus = e.httpStatus,
        code = e.code,
        message = e.message
    )
}