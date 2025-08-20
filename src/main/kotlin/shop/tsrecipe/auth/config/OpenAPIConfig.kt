package shop.tsrecipe.auth.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Auth Service",
        description = "Auth Server API Document"
    )
)
@Configuration
class OpenAPIConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val appOsSecuritySchemeName = "AppPlatform"
        val appOsSecurityScheme = SecurityScheme()
            .name("App-Platform")
            .description("""
                **참고) 클라이언트 OS 타입 (android / ios)**
            """.trimIndent())
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)

        val securityRequirement = SecurityRequirement()
            .addList(appOsSecuritySchemeName)

        val components = Components()
            .addSecuritySchemes(appOsSecuritySchemeName, appOsSecurityScheme)

        return OpenAPI()
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}