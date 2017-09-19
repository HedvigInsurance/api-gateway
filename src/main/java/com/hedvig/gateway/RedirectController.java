package com.hedvig.gateway;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
     
	private static Logger log = LoggerFactory.getLogger(RedirectController.class);
	
	@GetMapping("/login")
	String uid(HttpSession session) {
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		if (uid == null) {
			uid = UUID.randomUUID();
		}
		session.setAttribute(GatewayApplication.HEDVIG_SESSION, uid);
		HedvigToken hid = login(uid);
		log.debug("SessionID: " + uid.toString() + " UserID: " + hid);
		
		return "SessionID: " + uid.toString() + " UserID: " + hid;
	}
	
	@GetMapping("/logout")
	String logout(HttpSession session) {
		try {isLoggedIn(session);} catch (NotLoggedInException e) {return e.toString();}
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		GatewayApplication.sessionMap.remove(uid);
		session.removeAttribute(GatewayApplication.HEDVIG_SESSION);
		return "You are logged out";
	}
	
    @Value("${error.path:/error}")
    private String errorPath;
    
    @RequestMapping(value = "${error.path:/error}", produces = "application/vnd.error+json")
    public @ResponseBody ResponseEntity error(HttpServletRequest request) {
 
        return ResponseEntity.status(500).body("error");
    }
	
	private void isLoggedIn(HttpSession session) throws NotLoggedInException{
		UUID uid = (UUID) session.getAttribute(GatewayApplication.HEDVIG_SESSION);
		if(uid == null)throw new NotLoggedInException("Not logged in");
	}
	
	private HedvigToken login(UUID sessionID){
		HedvigToken hid = GatewayApplication.sessionMap.get(sessionID);
		if(hid == null){
			hid = new HedvigToken();
			GatewayApplication.sessionMap.put(sessionID, hid);
		}
		return hid;
	}
    

}