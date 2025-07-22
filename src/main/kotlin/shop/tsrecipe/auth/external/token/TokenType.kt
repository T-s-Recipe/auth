package shop.tsrecipe.auth.external.token

import java.time.Duration
import java.time.temporal.ChronoUnit

enum class TokenType(val expiration: Long, val unit: ChronoUnit) {

    ACCESS(1, ChronoUnit.HOURS),
    REFRESH(30, ChronoUnit.DAYS)
    ;

    val duration: Duration
        get() = Duration.of(this.expiration, this.unit)
}