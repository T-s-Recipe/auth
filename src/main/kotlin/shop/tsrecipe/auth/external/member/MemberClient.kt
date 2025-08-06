package shop.tsrecipe.auth.external.member

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import shop.tsrecipe.auth.api.OAuthProvider
import shop.tsrecipe.auth.config.MemberFeignConfig

@ReactiveFeignClient(
    name = "member-service",
    url = "\${external-service-url.member}",
    configuration = [MemberFeignConfig::class]
)
interface MemberClient {
    @GetMapping("/")
    fun getMember(
        @RequestParam("memberId") memberId: String?,
        @RequestParam("oAuthProvider") oAuthProvider: OAuthProvider?,
        @RequestParam("oAuthId") oAuthId: String?
    ): Mono<MemberResponse>
}