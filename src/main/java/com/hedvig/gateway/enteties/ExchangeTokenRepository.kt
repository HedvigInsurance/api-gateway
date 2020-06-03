package com.hedvig.gateway.enteties

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ExchangeTokenRepository : CrudRepository<ExchangeToken, String> {
  fun findByMemberId(memberId: String): List<ExchangeToken>
}