package com.hedvig.gateway.enteties

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["token"])])
class ExchangeToken(
  @Id @GeneratedValue
  val id: Long,
  val token: String,
  val memberId: String,
  val validity: Long
) {
  constructor(
    token: String,
    memberId: String,
    validity: Long
  ) : this(
    id = 0,
    token = token,
    memberId = memberId,
    validity = validity
  )

  @CreationTimestamp
  var createdAt: Instant = Instant.now()

  fun isValid(): Boolean {
    return createdAt.plusSeconds(validity) >= Instant.now()
  }
}