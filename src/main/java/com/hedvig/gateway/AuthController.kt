package com.hedvig.gateway

import com.hedvig.gateway.config.ServiceAccountTokenConfig
import com.hedvig.gateway.dto.CreateExchangeableTokenRequest
import com.hedvig.gateway.dto.CreateExchangeableTokenResponse
import com.hedvig.gateway.dto.ReassignMemberRequest
import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.service.ExchangeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("_")
class AuthController(
  private val repo: AuthorizationRowRepository,
  private val exchangeService: ExchangeService,
  private val config: ServiceAccountTokenConfig
) {
  @PostMapping("reassign")
  fun reassignMember(
    @RequestHeader("token") memberServiceToken: String,
    @RequestBody request: ReassignMemberRequest
  ): ResponseEntity<Void> {
    if (!isValidToken(memberServiceToken)) {
      return ResponseEntity.status(401).build()
    }

    val row = repo.findByMemberId(request.oldMemberId) ?: return ResponseEntity.notFound().build()
    row.memberId = request.newMemberId

    repo.save(row)
    return ResponseEntity.ok().build()
  }

  @PostMapping("exchangeableToken/create")
  fun createExchangeableToken(
    @RequestHeader("token") paymentServiceToken: String,
    @RequestBody request: CreateExchangeableTokenRequest
  ): ResponseEntity<CreateExchangeableTokenResponse> {
    if (!isValidToken(paymentServiceToken)) {
      return ResponseEntity.status(401).build()
    }

    val exchangeToken = exchangeService.createExchangeToken(request.memberId)
    return ResponseEntity.ok(CreateExchangeableTokenResponse(exchangeToken = exchangeToken))
  }

  private fun isValidToken(token: String): Boolean = config.tokens.contains(token)
}
