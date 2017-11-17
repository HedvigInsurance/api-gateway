package com.hedvig.gateway;

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

    @Autowired
    public RedirectController(RestTemplate restTemplate) {
	    this.restTemplate = restTemplate;
    }

    @PostMapping("/helloHedvig")
	ResponseEntity<String> login(@RequestBody(required = false) String json) throws NoSuchAlgorithmException {

    	log.info("Post parameter from client:");
    	log.info(json);
		ResponseEntity<HelloHedvigResponse> memberResponse = restTemplate.postForEntity("http://member-service/member/helloHedvig", "", HelloHedvigResponse.class);
        if(memberResponse.getStatusCode() == HttpStatus.OK) {

            HttpHeaders headers = new HttpHeaders();
            headers.add("hedvig.token", memberResponse.getBody().memberId.toString());
            headers.add("Content-Type", "application/json");
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

		GatewayApplication.sessionMap.remove(getJwt(authheader));
		return "{\"message\":\"You are logged out\"}";
	}
	
	// ---- Mock values TODO: for implmenetation in other servcies  -------- //

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

	@PostMapping("/push-token")
	ResponseEntity<Void> pushToken(@RequestBody String tokenJson) {
		return ResponseEntity.noContent().build();
	}
	
 	// --------------------------------------------------------------------- //
	
    @Value("${error.path:/error}")
    private String errorPath;
    
    @RequestMapping(value = "${error.path:/error}", produces = "application/vnd.error+json")
    public @ResponseBody ResponseEntity error(HttpServletRequest request) {
 
        return ResponseEntity.status(500).body("error");
    }
	
	private static void isLoggedIn(String authHeader) throws NotLoggedInException{
        String jwt = getJwt(authHeader);

		if(jwt == null || GatewayApplication.sessionMap.get(jwt) == null)throw new NotLoggedInException("Not logged in");
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
	private static HedvigToken assignJWT(String sessionID, String userId){
		HedvigToken hid = new HedvigToken();
        hid.setToken(userId);

		GatewayApplication.sessionMap.put(sessionID, hid);
		
		return hid;
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