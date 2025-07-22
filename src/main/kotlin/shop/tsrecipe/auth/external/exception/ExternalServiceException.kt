package shop.tsrecipe.auth.external.exception

import org.springframework.http.HttpStatus

sealed class ExternalServiceException(
    val httpStatus: HttpStatus = HttpStatus.SERVICE_UNAVAILABLE,
    override val message: String,
    val code: String? = null
) : RuntimeException() {
    class ExternalResponseParsingException(message: String) :
        ExternalServiceException(httpStatus = HttpStatus.BAD_GATEWAY, message = message)

    class ExternalServiceUnavailable(serviceName: String) :
        ExternalServiceException(message = "${serviceName}-service unavailable.")
}

fun ExternalServiceException.getLogMessage(): String {
    return "ErrorCode: ${this.code}\n ErrorMessage:${this.message}\n"
}