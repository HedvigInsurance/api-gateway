package com.hedvig.gateway.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.gateway.graphql.types.ExchangeTokenInput
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import org.springframework.stereotype.Component

@Component
class Mutation() : GraphQLMutationResolver {
  fun exchangeToken(input: ExchangeTokenInput): ExchangeTokenResponse {
    return ExchangeTokenResponse.ExchangeTokenFailureResponse("No reason")
  }
}