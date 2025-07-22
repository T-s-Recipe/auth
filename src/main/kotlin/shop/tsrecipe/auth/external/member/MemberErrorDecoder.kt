package shop.tsrecipe.auth.external.member

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus
import shop.tsrecipe.auth.exception.BaseException
import shop.tsrecipe.auth.exception.ErrorResponse
import shop.tsrecipe.auth.external.exception.ExternalServiceException.ExternalResponseParsingException
import shop.tsrecipe.auth.external.exception.ExternalServiceException.ExternalServiceUnavailable

class MemberErrorDecoder(
    private val objectMapper: ObjectMapper
) : ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        val httpStatus = HttpStatus.resolve(response.status()) ?: HttpStatus.INTERNAL_SERVER_ERROR

        if (httpStatus.is5xxServerError) {
            return ExternalServiceUnavailable("member")
        }

        val errorResponse: ErrorResponse = try {
            objectMapper.readValue(response.body().asInputStream(), ErrorResponse::class.java)
        } catch (e: Exception) {
            return ExternalResponseParsingException("Response parsing error from member-service.")
        }

        return BaseException(
            httpStatus = httpStatus,
            code = errorResponse.code,
            message = errorResponse.message
        )
    }
}