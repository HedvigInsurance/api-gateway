package com.hedvig.gateway.service

import com.hedvig.gateway.enteties.AuthorizationRow
import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.enteties.ExchangeToken
import com.hedvig.gateway.enteties.ExchangeTokenRepository
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
      token = tokenService.createJWT(),
      memberId = memberId,
      validity = ONE_DAY * 10
    )

    exchangeTokenRepository.save(exchangeToken)
    return exchangeToken.token
  }

  override fun exchangeToken(exchangeToken: String): String? {
    val activeExchangeTokenMaybe = exchangeTokenRepository.findById(exchangeToken).filter { it.isValid() }

    if (!activeExchangeTokenMaybe.isPresent) {
      return null
    }

    val memberId = activeExchangeTokenMaybe.get().memberId

    return authorizationRowRepository.findByMemberId(memberId).token
  }

  companion object {
    const val ONE_DAY = 86_400L
  }
}