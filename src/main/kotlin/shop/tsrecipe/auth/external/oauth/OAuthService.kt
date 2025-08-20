package shop.tsrecipe.auth.external.oauth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import org.springframework.stereotype.Component
import shop.tsrecipe.auth.api.AppPlatform
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.exception.BaseException
import shop.tsrecipe.auth.exception.ErrorCode
import shop.tsrecipe.auth.properties.AppPlatformProperties
import shop.tsrecipe.auth.properties.OAuthProperties
import shop.tsrecipe.auth.util.Logging
import java.net.URL
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class OAuthService(
    private val oauthProperties: OAuthProperties,
    private val appPlatformProperties: AppPlatformProperties
) : Logging {
    private val keyProvider = ConcurrentHashMap<String, RSAKeyProvider>()

    suspend fun decodeToken(platform: AppPlatform, provider: OAuthProvider, idToken: String): DecodedJWT {
        return try {
            val providerDetails = oauthProperties.getDetails(provider)

            verify(
                keyProvider = getKeyProvider(providerDetails.jwksUri),
                clientId = appPlatformProperties.getClientId(platform, provider),
                issuer = providerDetails.issuer,
                idToken = idToken,
            )
        } catch (e: JWTVerificationException) {
            logger.info { "Invalid ID Token. platform: ${platform.name} / provider: ${provider.name}\n cause: ${e.cause}\n" }
            throw BaseException(ErrorCode.INVALID_OAUTH_TOKEN)
        }
    }

    private suspend fun getKeyProvider(jwksUri: String): RSAKeyProvider {
        return keyProvider.computeIfAbsent(jwksUri) { uri ->
            val jwkProvider = JwkProviderBuilder(URL(uri))
                .cached(10, 24, TimeUnit.HOURS)
                .build()

            object : RSAKeyProvider {
                override fun getPublicKeyById(keyId: String) = jwkProvider.get(keyId).publicKey as RSAPublicKey
                override fun getPrivateKey(): RSAPrivateKey? = null
                override fun getPrivateKeyId(): String? = null
            }
        }
    }

    private suspend fun verify(
        keyProvider: RSAKeyProvider,
        clientId: String,
        issuer: String,
        idToken: String
    ): DecodedJWT {
        val algorithm = Algorithm.RSA256(keyProvider)

        val verifier = JWT.require(algorithm)
            .withIssuer(issuer, issuer.removePrefix("https://"))
            .withAudience(clientId)
            .build()

        return verifier.verify(idToken)
    }

    suspend fun getUserInfoByToken(provider: OAuthProvider, decodedJWT: DecodedJWT): OAuthUserInfo {
        return when (provider) {
            OAuthProvider.GOOGLE -> {
                OAuthUserInfo(
                    id = decodedJWT.subject,
                    emailVerified = decodedJWT.getClaim("email_verified").asBoolean()
                )
            }

            OAuthProvider.APPLE -> {
                OAuthUserInfo(
                    id = decodedJWT.subject,
                    emailVerified = decodedJWT.getClaim("email_verified").asString() == "true"
                )
            }
        }
    }
}