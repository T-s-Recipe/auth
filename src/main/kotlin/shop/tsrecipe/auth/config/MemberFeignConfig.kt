package shop.tsrecipe.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shop.tsrecipe.auth.external.member.MemberErrorDecoder

@Configuration
class MemberFeignConfig {
    @Bean
    fun memberErrorDecoder(objectMapper: ObjectMapper): MemberErrorDecoder {
        return MemberErrorDecoder(objectMapper)
    }
}