package com.hedvig.gateway.intergration.memberService

import com.hedvig.gateway.dto.HelloHedvigResponse

interface MemberService {
  fun helloHedvig(json: String?, acceptLanguage: String?): HelloHedvigResponse?
}