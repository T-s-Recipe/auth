package shop.tsrecipe.auth.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import shop.tsrecipe.auth.api.AppPlatform
import shop.tsrecipe.auth.api.OAuthProvider

@ConfigurationProperties(prefix = "app-platform-properties")
data class AppPlatformProperties(
    val platform: Map<String, ClientId>
) {
    data class ClientId(
        val google: String,
        val apple: String
    )

    fun getClientId(platform: AppPlatform, provider: OAuthProvider): String {
        val clientIds = this.platform[platform.name]!!

        return when (provider) {
            OAuthProvider.GOOGLE -> clientIds.google
            OAuthProvider.APPLE -> clientIds.apple
        }
    }
}