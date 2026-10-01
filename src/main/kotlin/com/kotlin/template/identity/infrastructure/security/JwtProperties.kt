package com.kotlin.template.identity.infrastructure.security

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@ConfigurationProperties("app.security.jwt")
class JwtProperties(
    @field:NotBlank val secret: String,
    @field:NotBlank val issuer: String,
    val accessTokenTtl: Duration = Duration.ofMinutes(15),
) {
    init {
        require(accessTokenTtl >= Duration.ofSeconds(1)) { "JWT access token TTL must be at least one second" }
    }
}

@ConfigurationProperties("app.security.cors")
class CorsProperties(val allowedOrigins: List<String> = emptyList())
