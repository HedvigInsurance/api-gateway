package com.hedvig.gateway.config

import ch.qos.logback.access.tomcat.LogbackValve

import javax.servlet.Filter

import org.apache.catalina.Valve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LogBackAccess {

  @Bean(name = ["TeeFilter"])
  fun teeFilter(): Filter {
    return ch.qos.logback.access.servlet.TeeFilter()
  }

  @Bean
  fun servletContainer(valves: List<Valve>): ServletWebServerFactory {
    val tomcat = TomcatServletWebServerFactory()

    val logbackValve = LogbackValve()

    // point to logback-access.xml
    logbackValve.filename = "logback-access.xml"

    tomcat.addContextValves(logbackValve)

    for (v in valves) {
      tomcat.addContextValves(v)
    }

    return tomcat
  }
}
