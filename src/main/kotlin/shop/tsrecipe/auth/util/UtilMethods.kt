package shop.tsrecipe.auth.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentTimestamp(): String {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

fun <T> baseResponse(status: HttpStatus? = HttpStatus.OK, body: T): ResponseEntity<T> {
    return ResponseEntity.status(status!!).body(body)
}