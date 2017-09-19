package com.hedvig.gateway;

import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.hedvig.gateway.filter.pre.SessionControllerFilter;

@EnableZuulProxy
@SpringBootApplication
public class GatewayApplication {

	private static Logger log = LoggerFactory.getLogger(GatewayApplication.class);
	static TreeMap<UUID, HedvigToken> sessionMap = new TreeMap<UUID, HedvigToken>();
	public static final String HEDVIG_SESSION ="hedvig.session";
	
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
	public static HedvigToken getToken(UUID uid) throws NotLoggedInException{
		log.info("Requesting getToken with uid:" + uid);
		if(uid == null)throw new NotLoggedInException("Not logged in");
		HedvigToken hid = GatewayApplication.sessionMap.get(uid);
		return hid;
	}

    @Bean
    public SessionControllerFilter payloadFilter() {
        return new SessionControllerFilter();
      }
 
}