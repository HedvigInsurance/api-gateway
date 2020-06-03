package com.hedvig.gateway.service

import com.hedvig.gateway.graphql.types.ExchangeTokenResponse

interface ExchangeService {
  fun createExchangeToken(memberId: String): String
  fun exchangeToken(exchangeToken: String): ExchangeTokenResponse
}