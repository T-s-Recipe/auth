package shop.tsrecipe.auth.external.member

import shop.tsrecipe.auth.api.OAuthProvider

data class GetMemberRequest(
    val memberId: String? = null,
    val oAuthProvider: OAuthProvider? = null,
    val oAuthId: String? = null
)

data class MemberResponse(
    val id: String,
    val oauthProvider: OAuthProvider,
    val oauthId: String,
    val name: String,
    val email: String,
    val nickname: String
)