package shop.tsrecipe.auth.external.member

import feign.QueryMap
import org.springframework.web.bind.annotation.GetMapping
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import shop.tsrecipe.auth.config.MemberFeignConfig

@ReactiveFeignClient(
    name = "member-service",
    url = "\${external-service-url.member}",
    configuration = [MemberFeignConfig::class]
)
interface MemberClient {
    @GetMapping
    fun getMember(@QueryMap request: GetMemberRequest): Mono<MemberResponse>
}