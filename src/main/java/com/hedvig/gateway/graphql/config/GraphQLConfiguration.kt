package com.hedvig.gateway.graphql.config

import com.coxautodev.graphql.tools.SchemaParserDictionary
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfiguration {
  @Bean
  fun schemaParserDictionary(): SchemaParserDictionary {
    return SchemaParserDictionary().add(
      dictionary = listOf(
        ExchangeTokenResponse.ExchangeTokenSuccessResponse::class.java,
        ExchangeTokenResponse.ExchangeTokenExpiredResponse::class.java,
        ExchangeTokenResponse.ExchangeTokenInvalidResponse::class.java
      )
    )
  }
}