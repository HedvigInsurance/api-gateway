package com.hedvig.gateway

import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.filter.post.MemberAuthFilter
import com.hedvig.gateway.filter.pre.SessionControllerFilter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@EnableZuulProxy
@EnableFeignClients
@SpringBootApplication
class GatewayApplication {

  @Bean
  fun payloadFilter(
    authorizationRowRepository: AuthorizationRowRepository
  ): SessionControllerFilter {
    return SessionControllerFilter(authorizationRowRepository)
  }

  @Bean
  fun memberAuthFilter(authorizationRowRepository: AuthorizationRowRepository): MemberAuthFilter {
    return MemberAuthFilter(authorizationRowRepository)
  }

  @LoadBalanced
  @Bean
  fun restTemplate(): RestTemplate {
    return RestTemplate()
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      SpringApplication.run(GatewayApplication::class.java, *args)
    }
  }
}
