package com.hedvig.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "hedvig.service")
class ServiceAccountTokenConfig {
  lateinit var tokens: List<String>
}