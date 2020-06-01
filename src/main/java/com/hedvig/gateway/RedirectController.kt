package com.hedvig.gateway

import com.hedvig.gateway.enteties.AuthorizationRow
import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.intergration.memberService.MemberService
import com.hedvig.gateway.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class RedirectController @Autowired
constructor(
  private val repo: AuthorizationRowRepository,
  private val memberService: MemberService,
  private val tokenService: TokenService
) {

  @Value("\${error.path:/error}")
  private val errorPath: String? = null

  @PostMapping("/helloHedvig")
  fun login(
    @RequestHeader(value = "Accept-Language", required = false) acceptLanguage: String,
    @RequestBody(required = false) json: String?
  ): ResponseEntity<String> {
    val responseFromHelloHedvig = memberService.helloHedvig(acceptLanguage, json)
      ?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()

    var jwt: String

    do {
      jwt = tokenService.createJWT()
      if (repo.existsById(jwt)) {
        log.error("Duplicate token: {}", jwt)
      } else {
        break
      }
    } while (true)

    val authorizationRow = AuthorizationRow()
    authorizationRow.token = jwt
    authorizationRow.memberId = responseFromHelloHedvig.memberId

    repo.save(authorizationRow)

    return ResponseEntity.ok(jwt)
  }

  @PostMapping(value = ["/logout"], produces = ["application/json; charset=utf-8"])
  fun logout(@RequestHeader("Authorization") authheader: String): String {
    try {
      isLoggedIn(authheader)
    } catch (e: NotLoggedInException) {
      return "{\"message:\": \"You are not logged in\"}"
    }

    repo.deleteById(getJwt(authheader)!!)
    return "{\"message\":\"You are logged out\"}"
  }

  @GetMapping("/health")
  internal fun health(): ResponseEntity<*> {
    return ResponseEntity.ok("")
  }

  @RequestMapping(value = ["\${error.path:/error}"], produces = ["application/vnd.error+json"])
  @ResponseBody
  fun error(request: HttpServletRequest): ResponseEntity<*> {

    return ResponseEntity.status(500).body("error")
  }

  @Throws(NotLoggedInException::class)
  private fun isLoggedIn(authHeader: String) {
    getJwt(authHeader) ?: throw NotLoggedInException("Not logged in")
  }

  companion object {
    val log = LoggerFactory.getLogger(RedirectController::class.java)!!

    private fun getJwt(authHeader: String?): String? {
      var jwt: String? = null
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        jwt = authHeader.substring(7)
      }
      return jwt
    }
  }
}
