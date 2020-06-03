package com.hedvig.gateway.service

import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.Base64

@Service
class TokenServiceImpl : TokenService {
  override fun createJWT(): String {
    var jwt = ""
    for (i in 1..3) {
      val r = SecureRandom()
      val bytes = ByteArray(10)
      val adapter = Base64.getEncoder()
      r.nextBytes(bytes)
      jwt += adapter.encodeToString(bytes)
      if (i < 3) jwt += "."
    }
    return jwt
  }

  override fun createPrefixedJWT(prefix: String): String {
    val jwt = createJWT()
    return prefix + jwt
  }
}