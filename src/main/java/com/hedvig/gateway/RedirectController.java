package com.hedvig.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class HelloHedvigResponse {
	public Long memberId;
}

@RestController
public class RedirectController {
     
	private static Logger log = LoggerFactory.getLogger(RedirectController.class);

	private static ConcurrentHashMap<UUID, String> collectMap = new ConcurrentHashMap<>();
	
	@PostMapping("/helloHedvig")
	String login() throws NoSuchAlgorithmException {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<HelloHedvigResponse> response = restTemplate.postForEntity("http://memer-service/member/helloHedvig", "", HelloHedvigResponse.class);

		String jwt = createJWT();
		assignJWT(jwt, response.getBody().memberId.toString());

		return jwt;
	}

	@GetMapping("/logout")
	String logout(HttpSession session) {
		try {isLoggedIn(session);} catch (NotLoggedInException e) {return e.toString();}
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		GatewayApplication.sessionMap.remove(uid);
		session.removeAttribute(GatewayApplication.HEDVIG_SESSION);
		return "You are logged out";
	}
	
	// ---- Mock values TODO: for implmenetation in other servcies  -------- //
	
	@GetMapping("/insurance")
    ResponseEntity<String> getInsurance() {
		return ResponseEntity.ok("\"{\"insurance\":{\"fire\":{\"state\":\"waiting_for_payment\",\"included_in_base_package\":false,},\"theft\":{\"state\":\"disabled\",\"included_in_base_package\":false},\"waterleak\":{\"state\":\"waiting_for_signing\",\"included_in_base_package\":true},\"assets\":[{\"id\":\"12312412\",\"image_urls\":[...],\"name\":\"Kamera\"\"state\":\"enabled\",\"included_in_base_package\":true},{\"id\":\"1231241124122\",\"image_urls\":[...],\"name\":\"Laptop\"\"state\":\"waiting_for_signing\",\"included_in_base_package\":false}]\"current_total_price\":500\"new_total_price\":600}}}");
	}
	
	// --------------------------------------------------------------------- //
	
    @Value("${error.path:/error}")
    private String errorPath;
    
    @RequestMapping(value = "${error.path:/error}", produces = "application/vnd.error+json")
    public @ResponseBody ResponseEntity error(HttpServletRequest request) {
 
        return ResponseEntity.status(500).body("error");
    }
	
	private static void isLoggedIn(HttpSession session) throws NotLoggedInException{
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		if(uid == null)throw new NotLoggedInException("Not logged in");
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