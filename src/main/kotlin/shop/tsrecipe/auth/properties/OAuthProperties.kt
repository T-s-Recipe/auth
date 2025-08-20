package shop.tsrecipe.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import shop.tsrecipe.auth.api.OAuthProvider

@ConfigurationProperties(prefix = "oauth-properties")
class OAuthProperties(
    val provider: Map<String, ProviderDetails>
) {
    data class ProviderDetails(
        val jwksUri: String,
        val issuer: String
    )

    fun getDetails(provider: OAuthProvider): ProviderDetails {
        return this.provider[provider.name.lowercase()]!!
    }
}