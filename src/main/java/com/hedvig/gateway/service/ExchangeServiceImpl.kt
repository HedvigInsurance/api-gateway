package com.hedvig.gateway.service

import com.hedvig.gateway.enteties.AuthorizationRow
import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.enteties.ExchangeToken
import com.hedvig.gateway.enteties.ExchangeTokenRepository
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import org.springframework.stereotype.Service

@Service
class ExchangeServiceImpl(
  val authorizationRowRepository: AuthorizationRowRepository,
  val exchangeTokenRepository: ExchangeTokenRepository,
  val tokenService: TokenService
) : ExchangeService {
  override fun createExchangeToken(memberId: String): String {
    var tokenMaybe = authorizationRowRepository.findByMemberId(memberId)

    if (tokenMaybe == null) {
      tokenMaybe = AuthorizationRow().also {
        it.token = tokenService.createJWT()
        it.memberId = memberId
      }
      authorizationRowRepository.save(tokenMaybe)
    }

    val exchangeToken = ExchangeToken(
      token = tokenService.createPrefixedJWT(PREFIX),
      memberId = memberId,
      validity = ONE_DAY * 10
    )

    exchangeTokenRepository.save(exchangeToken)
    return exchangeToken.token
  }

  override fun exchangeToken(exchangeToken: String): ExchangeTokenResponse {
    val exchangeTokenMaybe = exchangeTokenRepository.findById(exchangeToken)

    if (!exchangeTokenMaybe.isPresent) {
      return ExchangeTokenResponse.ExchangeTokenInvalidResponse
    }

    val activeExchangeTokenMaybe = exchangeTokenMaybe.filter { it.isValid() }

    if (!activeExchangeTokenMaybe.isPresent) {
      return ExchangeTokenResponse.ExchangeTokenExpiredResponse
    }

    val memberId = activeExchangeTokenMaybe.get().memberId

    val token: String = authorizationRowRepository.findByMemberId(memberId)?.token
      ?: return ExchangeTokenResponse.ExchangeTokenInvalidResponse

    return ExchangeTokenResponse.ExchangeTokenSuccessResponse(token = token)
  }

  companion object {
    const val ONE_DAY = 86_400L
    const val PREFIX = "EX_"
  }
}