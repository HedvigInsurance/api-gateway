package com.hedvig.gateway;

import com.hedvig.gateway.enteties.AuthorizationRow;
import com.hedvig.gateway.enteties.AuthorizationRowRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

class HelloHedvigResponse {
  public Long memberId;
}

@RestController
public class RedirectController {

  private static Logger log = LoggerFactory.getLogger(RedirectController.class);
  private final RestTemplate restTemplate;
  private final AuthorizationRowRepository repo;

  @Value("${error.path:/error}")
  private String errorPath;

  @Autowired
  public RedirectController(RestTemplate restTemplate, AuthorizationRowRepository repo) {
    this.restTemplate = restTemplate;
    this.repo = repo;
  }

  private static String getJwt(String authHeader) {
    String jwt = null;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwt = authHeader.substring(7);
    }
    return jwt;
  }

  // ---- Mock values TODO: for implmenetation in other servcies  -------- //

  @PostMapping("/helloHedvig")
  ResponseEntity<String> login(@RequestBody(required = false) String json)
      throws NoSuchAlgorithmException {

    log.info("Post parameter from client:");
    log.info(json);
    ResponseEntity<HelloHedvigResponse> memberResponse =
        restTemplate.postForEntity(
            "http://member-service/member/helloHedvig", "", HelloHedvigResponse.class);
    if (memberResponse.getStatusCode() == HttpStatus.OK) {

      HttpHeaders headers = new HttpHeaders();
      headers.add("hedvig.token", memberResponse.getBody().memberId.toString());
      headers.add("Content-Type", "application/json; charset=utf-8");
      HttpEntity<String> httpEntity = new HttpEntity<>(json == null ? "" : json, headers);
      ResponseEntity<Void> botResponse =
          restTemplate.exchange("http://bot-service/init", HttpMethod.POST, httpEntity, Void.class);

      if (botResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
        String jwt;
        do {
          jwt = createJWT();
          if (repo.exists(jwt)) {
            log.error("Duplicate token: {}", jwt);
          } else {
            break;
          }
        } while (true);

        AuthorizationRow authorizationRow = new AuthorizationRow();
        authorizationRow.token = jwt;
        authorizationRow.memberId = memberResponse.getBody().memberId.toString();

        repo.save(authorizationRow);
        // GatewayApplication.sessionMap.put(sessionID, hid);

        return ResponseEntity.ok(jwt);
      }
    }

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("{\"message\":\"Internal error, please try again.");
  }

  @PostMapping(value = "/logout", produces = "application/json; charset=utf-8")
  String logout(@RequestHeader("Authorization") String authheader) {
    try {
      isLoggedIn(authheader);
    } catch (NotLoggedInException e) {
      return "{\"message:\": \"You are not logged in\"}";
    }
    repo.delete(getJwt(authheader));
    return "{\"message\":\"You are logged out\"}";
  }

  /*
   * Creates a fake jwt token.
   */
  private String createJWT() {
    String jwt = "";
    for (int i = 1; i <= 3; i++) {
      Random r = new Random();
      byte[] bytes = new byte[10];
      BASE64Encoder adapter = new BASE64Encoder();
      r.nextBytes(bytes);
      jwt += adapter.encode(bytes);
      if (i < 3) jwt += ".";
    }
    return jwt;
  }

  @GetMapping("/health")
  ResponseEntity<?> health() {
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "${error.path:/error}", produces = "application/vnd.error+json")
  public @ResponseBody ResponseEntity error(HttpServletRequest request) {

    return ResponseEntity.status(500).body("error");
  }

  private void isLoggedIn(String authHeader) throws NotLoggedInException {
    String jwt = getJwt(authHeader);

    if (jwt == null || repo.findOne(jwt) == null) throw new NotLoggedInException("Not logged in");
  }
}
