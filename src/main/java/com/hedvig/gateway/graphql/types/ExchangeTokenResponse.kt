package com.hedvig.gateway.graphql.types

sealed class ExchangeTokenResponse {

  data class ExchangeTokenSuccessResponse(
    val token: String
  ) : ExchangeTokenResponse()

  object ExchangeTokenInvalidResponse : ExchangeTokenResponse()

  object ExchangeTokenExpiredResponse : ExchangeTokenResponse()
}
