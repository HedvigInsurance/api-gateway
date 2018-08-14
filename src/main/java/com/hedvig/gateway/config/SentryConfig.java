package com.hedvig.gateway.config;

import io.sentry.spring.SentryExceptionResolver;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@Profile("production")
public class SentryConfig {
  @Bean
  public HandlerExceptionResolver sentryExceptionResolver() {
    return new SentryExceptionResolver();
  }

  @Bean
  public ServletContextInitializer sentryServletContextInitializer() {
    return new io.sentry.spring.SentryServletContextInitializer();
  }
}
