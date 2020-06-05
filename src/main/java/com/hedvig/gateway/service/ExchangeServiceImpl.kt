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
    var tokenMaybe = authorizationRowRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)

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
    val exchangeTokenEntity = exchangeTokenRepository.findByToken(exchangeToken)
      ?: return ExchangeTokenResponse.ExchangeTokenInvalidResponse

    if (!exchangeTokenEntity.isValid()) {
      return ExchangeTokenResponse.ExchangeTokenExpiredResponse
    }

    val memberId = exchangeTokenEntity.memberId

    val token: String = authorizationRowRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)?.token
      ?: return ExchangeTokenResponse.ExchangeTokenInvalidResponse

    return ExchangeTokenResponse.ExchangeTokenSuccessResponse(token = token)
  }

  companion object {
    const val ONE_DAY = 86_400L
    const val PREFIX = "EX_"
  }
}
