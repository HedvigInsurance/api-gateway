package com.hedvig.gateway.service

interface TokenService {
  fun createJWT(): String
  fun createPrefixedJWT(prefix: String): String
}