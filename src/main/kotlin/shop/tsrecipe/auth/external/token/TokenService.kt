package shop.tsrecipe.auth.external.token

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import shop.tsrecipe.auth.external.redis.RedisService
import shop.tsrecipe.auth.properties.JwtProperties
import java.time.Instant

@Service
class TokenService(
    private val jwtProperties: JwtProperties,
    private val redisService: RedisService
) {
    private val algorithm by lazy { Algorithm.HMAC256(jwtProperties.secretKey) }

    private val verifier: JWTVerifier by lazy {
        JWT.require(algorithm)
            .withIssuer(jwtProperties.issuer)
            .build()
    }

    suspend fun issue(memberId: String, type: TokenType): String {
        val now = Instant.now()
        return JWT.create()
            .withIssuer(jwtProperties.issuer)
            .withSubject(memberId)
            .withIssuedAt(now)
            .withExpiresAt(now.plus(type.expiration, type.unit))
            .sign(algorithm)
    }

    suspend fun cachingRefreshToken(memberId: String, token: String) {
        coroutineScope {
            launch {
                redisService.set(
                    prefix = "refresh-token",
                    key = memberId,
                    value = token,
                    duration = TokenType.REFRESH.duration
                )
            }
        }
    }

    suspend fun verify(token: String): String {
        return try {
            verifier.verify(token).subject
        } catch (e: JWTVerificationException) {
            throw e
        }
    }

    suspend fun revokeRefreshTokenIfVerified(token: String): String {
        val memberId = verify(token)
        deleteRefreshToken(memberId)

        return memberId
    }

    private suspend fun deleteRefreshToken(memberId: String) {
        redisService.delete(
            prefix = TokenType.REFRESH.name,
            key = memberId
        )
    }
}