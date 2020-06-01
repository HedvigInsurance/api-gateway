package com.hedvig.gateway.enteties

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ExchangeToken(
  @Id
  val token: String,
  val memberId: String,
  val validity: Long
) {
  @CreationTimestamp
  var createdAt: Instant = Instant.now()

  @UpdateTimestamp
  var updatedAt: Instant = Instant.now()


  fun isValid(): Boolean {
    return createdAt.plusSeconds(validity) >= Instant.now()
  }
}