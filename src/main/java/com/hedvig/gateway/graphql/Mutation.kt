package com.hedvig.gateway.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.gateway.graphql.types.ExchangeTokenInput
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import com.hedvig.gateway.service.ExchangeService
import org.springframework.stereotype.Component

@Component
class Mutation(
  val exchangeService: ExchangeService
) : GraphQLMutationResolver {
  fun exchangeToken(input: ExchangeTokenInput): ExchangeTokenResponse {
    return exchangeService.exchangeToken(input.exchangeToken)
  }
}