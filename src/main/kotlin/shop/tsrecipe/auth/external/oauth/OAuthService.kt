package shop.tsrecipe.auth.external.oauth

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.RSAKeyProvider
import org.springframework.stereotype.Component
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.properties.OAuthProperties
import shop.tsrecipe.auth.util.Logging
import java.net.URL
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class OAuthService(
    oauthProperties: OAuthProperties
) : Logging {
    private val providerMap = oauthProperties.providerMap
    private val keyProvider = ConcurrentHashMap<String, RSAKeyProvider>()

    suspend fun decodeToken(provider: OAuthProvider, idToken: String): DecodedJWT {
        val providerDetails = providerMap[provider.name.lowercase()]!!

        try {
            val keyProvider = getKeyProvider(providerDetails)
            val algorithm = Algorithm.RSA256(keyProvider)

            val verifier = JWT.require(algorithm)
                .withIssuer(providerDetails.issuer, providerDetails.issuer.removePrefix("https://"))
                .withAudience(providerDetails.clientId)
                .build()

            return verifier.verify(idToken)
        } catch (e: JWTVerificationException) {
            logger.info { "Invalid ID Token. provider: ${provider.name}\n" }
            throw e
        }
    }

    private suspend fun getKeyProvider(properties: OAuthProperties.ProviderDetails): RSAKeyProvider {
        return keyProvider.computeIfAbsent(properties.jwksUri) { uri ->
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

    suspend fun getUserInfoByToken(provider: OAuthProvider, decodedJWT: DecodedJWT): OAuthUserInfo {
        return when (provider) {
            OAuthProvider.GOOGLE -> {
                OAuthUserInfo(
                    id = decodedJWT.subject,
                    name = decodedJWT.getClaim("name").asString(),
                    email = decodedJWT.getClaim("email").asString(),
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