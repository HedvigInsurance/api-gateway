package com.hedvig.gateway.enteties

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ExchangeTokenRepository : CrudRepository<ExchangeToken, Long> {
  fun findByMemberId(memberId: String): List<ExchangeToken>
}