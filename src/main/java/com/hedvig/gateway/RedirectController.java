package com.hedvig.gateway;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

@RestController
public class RedirectController {
     
	private static Logger log = LoggerFactory.getLogger(RedirectController.class);

	private static ConcurrentHashMap<UUID, String> collectMap = new ConcurrentHashMap<>();
	
	@PostMapping("/authenticate")
	String login(@RequestParam String ssn) throws NoSuchAlgorithmException {

        String jwt = "";
	    for(int i =1; i<=3; i++) {
            Random r = new Random();
            byte[] bytes = new byte[10];
            BASE64Encoder adapter = new BASE64Encoder();
            r.nextBytes(bytes);
            jwt += adapter.encode(bytes);
            if(i<3)
                jwt += ".";
        }


		UUID collectUid = UUID.randomUUID();

		collectMap.put(collectUid, jwt);
		HedvigToken hid = assignJWT(jwt, ssn);

		log.debug("SessionID: " + jwt.toString() + " UserID: " + hid);
		
		return collectUid.toString();
	}
	
	@GetMapping("/logout")
	String logout(HttpSession session) {
		try {isLoggedIn(session);} catch (NotLoggedInException e) {return e.toString();}
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		GatewayApplication.sessionMap.remove(uid);
		session.removeAttribute(GatewayApplication.HEDVIG_SESSION);
		return "You are logged out";
	}

	@GetMapping("/collect")
    ResponseEntity<String> collect(@RequestParam UUID uuid) {
	    String jwt = collectMap.get(uuid);
	    if(jwt == null){
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(jwt);
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

}