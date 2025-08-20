package shop.tsrecipe.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import reactivefeign.spring.config.EnableReactiveFeignClients
import shop.tsrecipe.auth.properties.JwtProperties
import shop.tsrecipe.auth.properties.AppPlatformProperties
import shop.tsrecipe.auth.properties.ExternalServiceUrl
import shop.tsrecipe.auth.properties.OAuthProperties

@EnableConfigurationProperties(value = [JwtProperties::class, AppPlatformProperties::class, OAuthProperties::class, ExternalServiceUrl::class])
@EnableReactiveFeignClients
@SpringBootApplication
class AuthApplication

fun main(args: Array<String>) {
	runApplication<AuthApplication>(*args)
}