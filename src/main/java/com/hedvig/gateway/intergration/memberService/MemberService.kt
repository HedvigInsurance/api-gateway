package com.hedvig.gateway.intergration.memberService

import com.hedvig.gateway.dto.HelloHedvigResponse

interface MemberService {
  fun helloHedvig(acceptLanguage: String?, json: String?): HelloHedvigResponse?
}