package shop.tsrecipe.auth.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus? = HttpStatus.NOT_FOUND, var message: String) {
    UNDEFINED_EXCEPTION(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Sorry, undefined exception."),

    // OAuth
    NOT_VERIFIED_USER(status = HttpStatus.BAD_REQUEST, message = "Not verified user from OAuth service."),
    INVALID_OAUTH_TOKEN(status = HttpStatus.BAD_REQUEST, message = "Invalid OAuth token."),

    // external
    MEMBER_NOT_FOUND(message = "Member not found."),
    KEY_NOT_FOUND(message = "Redis key not found"),
}