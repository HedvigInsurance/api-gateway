package com.hedvig.gateway;

import com.hedvig.gateway.enteties.AuthorizationRow;
import com.hedvig.gateway.enteties.AuthorizationRowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

class HelloHedvigResponse {
	public Long memberId;
}

@RestController
public class RedirectController {
     
	private static Logger log = LoggerFactory.getLogger(RedirectController.class);
    private final RestTemplate restTemplate;
	private final AuthorizationRowRepository repo;

    @Autowired
    public RedirectController(RestTemplate restTemplate, AuthorizationRowRepository repo) {
	    this.restTemplate = restTemplate;
		this.repo = repo;
	}

    @PostMapping("/helloHedvig")
	ResponseEntity<String> login(@RequestBody(required = false) String json) throws NoSuchAlgorithmException {

    	log.info("Post parameter from client:");
    	log.info(json);
		ResponseEntity<HelloHedvigResponse> memberResponse = restTemplate.postForEntity("http://member-service/member/helloHedvig", "", HelloHedvigResponse.class);
        if(memberResponse.getStatusCode() == HttpStatus.OK) {

            HttpHeaders headers = new HttpHeaders();
            headers.add("hedvig.token", memberResponse.getBody().memberId.toString());
            headers.add("Content-Type", "application/json; charset=utf-8");
            HttpEntity<String> httpEntity = new HttpEntity<>(json == null ? "": json,headers);
            ResponseEntity<Void> botResponse = restTemplate.exchange("http://bot-service/init", HttpMethod.POST, httpEntity, Void.class);

            if(botResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
                String jwt = createJWT();
                assignJWT(jwt, memberResponse.getBody().memberId.toString());

                return ResponseEntity.ok(jwt);
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"Internal error, please try again.");
	}

	@PostMapping(value = "/logout", produces = "application/json; charset=utf-8")
	String logout(@RequestHeader("Authorization") String authheader) {
		try {
			isLoggedIn(authheader);
		}
		catch (NotLoggedInException e) {
			return "{\"message:\": \"You are not logged in\"}";
		}

		repo.delete(getJwt(authheader));
		return "{\"message\":\"You are logged out\"}";
	}
	
	// ---- Mock values TODO: for implmenetation in other servcies  -------- //


	@GetMapping("/health")
	ResponseEntity<?> health() {
    	return ResponseEntity.ok("");
	}

	@PostMapping("/checkout")
	ResponseEntity<Void> initiateUpdate() {
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update-family-members")
	ResponseEntity<Void> updateFamilyMembers() {
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update-personal-information")
	ResponseEntity<Void> updatePersonalInformation() {
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update-apartment-information")
	ResponseEntity<Void> updateApartmentInformation() {
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update-bank-account")
	ResponseEntity<Void> updateBankAccount() { return ResponseEntity.noContent().build(); }
	
 	// --------------------------------------------------------------------- //
	
    @Value("${error.path:/error}")
    private String errorPath;
    
    @RequestMapping(value = "${error.path:/error}", produces = "application/vnd.error+json")
    public @ResponseBody ResponseEntity error(HttpServletRequest request) {
 
        return ResponseEntity.status(500).body("error");
    }
	
	private void isLoggedIn(String authHeader) throws NotLoggedInException{
        String jwt = getJwt(authHeader);

		if(jwt == null || repo.findOne(jwt) == null)
			throw new NotLoggedInException("Not logged in");
	}

    private static String getJwt(String authHeader) {
        String jwt = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        return jwt;
    }

    /*
     * Log in with explicit user id. Replaces current hedvig.token in the session
     * */
	private void assignJWT(String sessionID, String userId){
		AuthorizationRow authorizationRow = new AuthorizationRow();
		authorizationRow.token = sessionID;
		authorizationRow.memberId = userId;

		if(repo.exists(sessionID)) {
			throw new RuntimeException();
		}
		repo.save(authorizationRow);
		//GatewayApplication.sessionMap.put(sessionID, hid);
		
		return;
	}

	/*
	 * Creates a fake jwt token.
	 */
	private String createJWT() {
		String jwt = "";
		for(int i =1; i<=3; i++) {
			Random r = new Random();
			byte[] bytes = new byte[10];
			BASE64Encoder adapter = new BASE64Encoder();
			r.nextBytes(bytes);
			jwt += adapter.encode(bytes);
			if (i < 3)
				jwt += ".";
		}
		return jwt;
	}

}