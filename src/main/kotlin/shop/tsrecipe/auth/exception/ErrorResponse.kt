package shop.tsrecipe.auth.exception

import shop.tsrecipe.auth.util.getCurrentTimestamp

class ErrorResponse(
    val code: String?,
    val message: String,
    val timestamp: String,
    ) {

    constructor(e: BaseException) : this(
        code = e.code,
        message = e.message,
        timestamp = e.timestamp
    )

    constructor(e: ErrorCode): this(
        code = e.name,
        message = e.message,
        timestamp = getCurrentTimestamp()
    )
}