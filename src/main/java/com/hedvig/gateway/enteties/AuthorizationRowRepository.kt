package com.hedvig.gateway.enteties

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorizationRowRepository : CrudRepository<AuthorizationRow, String> {
  fun findByMemberId(memberId: String): AuthorizationRow?
}