package com.hedvig.gateway

import com.hedvig.gateway.dto.ReassignMemberRequest
import com.hedvig.gateway.enteties.AuthorizationRow
import com.hedvig.gateway.enteties.AuthorizationRowRepository

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
  private val repo: AuthorizationRowRepository,
  @param:Value("\${member-service.token}") private val token: String
) {
  @PostMapping("/_/reassign")
  fun reassignMember(@RequestHeader("token") memberServiceToken: String, @RequestBody request: ReassignMemberRequest): ResponseEntity<Void> {
    if (memberServiceToken != token) {
      return ResponseEntity.status(401).build()
    }

    val row = repo.findByMemberId(request.oldMemberId) ?: return ResponseEntity.notFound().build()
    row.memberId = request.newMemberId

    repo.save(row)
    return ResponseEntity.ok().build()
  }
}
