package com.hedvig.gateway.graphql.types

sealed class ExchangeTokenResponse {

  data class ExchangeTokenSuccessResponse(
    val token: String
  ) : ExchangeTokenResponse()

  object ExchangeTokenInvalidResponse : ExchangeTokenResponse() {
    const val `_`: Boolean = true
  }

  object ExchangeTokenExpiredResponse : ExchangeTokenResponse() {
    const val `_`: Boolean = true
  }
}
