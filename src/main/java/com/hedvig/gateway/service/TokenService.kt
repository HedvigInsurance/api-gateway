package com.hedvig.gateway.service

interface TokenService {
  fun createJWT(): String
}