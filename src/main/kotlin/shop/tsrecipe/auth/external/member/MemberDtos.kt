package shop.tsrecipe.auth.external.member

import shop.tsrecipe.auth.api.OAuthProvider

data class MemberResponse(
    val id: String,
    val oauthProvider: OAuthProvider,
    val oauthId: String,
    val name: String,
    val email: String,
    val nickname: String
)