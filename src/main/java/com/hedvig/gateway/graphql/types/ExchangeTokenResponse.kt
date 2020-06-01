package com.hedvig.gateway.graphql.types

sealed class ExchangeTokenResponse {

  data class ExchangeTokenSuccessResponse(
    val token: String
  ) : ExchangeTokenResponse()

  data class ExchangeTokenFailureResponse(
    val reason: String
  ) : ExchangeTokenResponse()
}
