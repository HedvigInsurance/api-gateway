package com.hedvig.gateway;

import java.util.TreeMap;
import java.util.UUID;

import com.hedvig.gateway.filter.post.MemberAuthFilter;
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
	public static TreeMap<String, HedvigToken> sessionMap = new TreeMap<String, HedvigToken>();
	public static final String HEDVIG_SESSION ="hedvig.session";
	
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
	public static HedvigToken getToken(String jwt) throws NotLoggedInException{
		log.info("Requesting getToken with jwt:" + jwt);
		if(jwt == null || !GatewayApplication.sessionMap.containsKey(jwt))
			throw new NotLoggedInException("Not logged in");
		return GatewayApplication.sessionMap.get(jwt);
	}

    @Bean
    public SessionControllerFilter payloadFilter() {
        return new SessionControllerFilter();
      }

	@Bean
	public MemberAuthFilter memberAuthFilter() {
    	return new MemberAuthFilter();
	}
 
}