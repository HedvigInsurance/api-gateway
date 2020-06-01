package com.hedvig.gateway.service

import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Random

@Service
class TokenServiceImpl : TokenService {
  override fun createJWT(): String {
    var jwt = ""
    for (i in 1..3) {
      val r = Random()
      val bytes = ByteArray(10)
      val adapter = Base64.getEncoder()
      r.nextBytes(bytes)
      jwt += adapter.encodeToString(bytes)
      if (i < 3) jwt += "."
    }
    return jwt
  }
}