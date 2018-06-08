package com.hedvig.gateway;

import com.hedvig.gateway.enteties.AuthorizationRowRepository;
import com.hedvig.gateway.filter.post.MemberAuthFilter;
import com.hedvig.gateway.filter.pre.SessionControllerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableZuulProxy
@SpringBootApplication
//@EnableCaching
//Before enabling caching figure out how to remove an item from the cache.
public class GatewayApplication {

	private static Logger log = LoggerFactory.getLogger(GatewayApplication.class);
	public static final String HEDVIG_SESSION ="hedvig.session";
	
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

	@Bean
    public SessionControllerFilter payloadFilter(AuthorizationRowRepository authorizationRowRepository) {
        return new SessionControllerFilter(authorizationRowRepository);
      }

	@Bean
	public MemberAuthFilter memberAuthFilter(AuthorizationRowRepository authorizationRowRepository) {
    	return new MemberAuthFilter(authorizationRowRepository);
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
    	return new RestTemplate();
	}

	/*
	@CacheEvict(allEntries = true, value = {"authorizationRows"})
	@Scheduled(fixedDelay = 10 * 60 * 1000 ,  initialDelay = 500)
	public void reportCacheEvict() {
		System.out.println("Flush Cache " + Instant.now().toString());
	}*/

}