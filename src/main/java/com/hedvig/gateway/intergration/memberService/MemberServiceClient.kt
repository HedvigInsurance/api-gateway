package com.hedvig.gateway.intergration.memberService

import com.hedvig.gateway.dto.HelloHedvigResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "memberServiceClient", url = "\${hedvig.member-service.url:member-service}")
interface MemberServiceClient {

  @PostMapping(value = ["/v2/member/helloHedvig"])
  fun helloHedvig(): ResponseEntity<HelloHedvigResponse>

  @PostMapping(value = ["/v2/member/helloHedvig"])
  fun helloHedvig(@RequestBody json: String): ResponseEntity<HelloHedvigResponse>
}
