package com.hedvig.gateway.intergration.memberService

import com.hedvig.gateway.dto.HelloHedvigResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class MemberServiceImpl @Autowired constructor(
  val memberServiceClient: MemberServiceClient
) : MemberService {
  override fun helloHedvig(json: String?): HelloHedvigResponse? {
    val response = if (json == null) memberServiceClient.helloHedvig() else memberServiceClient.helloHedvig(json)
    if (response.statusCode == HttpStatus.OK) {
      return response.body
    }
    return null
  }
}