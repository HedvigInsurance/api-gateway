package com.hedvig.gateway.service

interface ExchangeService {
  fun createExchangeToken(memberId: String): String
  fun exchangeToken(exchangeToken: String): String?
}