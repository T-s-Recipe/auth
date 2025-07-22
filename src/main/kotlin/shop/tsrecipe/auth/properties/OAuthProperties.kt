package shop.tsrecipe.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth-provider")
data class OAuthProperties(
    val providerMap: Map<String, ProviderDetails>
) {
    data class ProviderDetails(
        val clientId: String,
        val jwksUri: String,
        val issuer: String
    )
}