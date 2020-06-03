package com.hedvig.gateway

import com.hedvig.gateway.config.ServiceAccountTokenConfig
import com.hedvig.gateway.dto.CreateSetupPaymentLinkRequest
import com.hedvig.gateway.dto.CreateSetupPaymentLinkResponse
import com.hedvig.gateway.service.ExchangeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("_")
class LinkGeneratorController(
  private val config: ServiceAccountTokenConfig,
  private val exchangeService: ExchangeService
) {
  @Value("\${hedvig.paymentSetupLink}")
  lateinit var paymentSetupLink: String

  @PostMapping("setupPaymentLink/create")
  fun createSetupPaymentLink(
    @RequestHeader("token") serviceToken: String,
    @RequestBody request: CreateSetupPaymentLinkRequest
  ): ResponseEntity<CreateSetupPaymentLinkResponse> {
    if (config.tokens.contains(serviceToken)) {
      return ResponseEntity.status(401).build()
    }

    val exchangeToken = exchangeService.createExchangeToken(request.memberId)

    val finalUrl = paymentSetupLink.replace(TOKEN_PLACEHOLDER, exchangeToken)

    return ResponseEntity.ok(CreateSetupPaymentLinkResponse(url = finalUrl))
  }

  companion object {
    const val TOKEN_PLACEHOLDER = "TOKEN_PLACEHOLDER"
  }
}