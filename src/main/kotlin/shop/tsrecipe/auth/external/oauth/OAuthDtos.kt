package shop.tsrecipe.auth.external.oauth

data class OAuthUserInfo(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val emailVerified: Boolean
)