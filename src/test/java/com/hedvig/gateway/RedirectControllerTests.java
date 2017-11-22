package com.hedvig.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GatewayApplication.class)
public class RedirectControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void EmptyRequest_IS_Unauthorized() throws Exception {

        assertThat(
            this.restTemplate.getForEntity("http://localhost:" + port + "/member/me", String.class)).
                hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }
// Disable these tests on travis since we cant talk to the other services yet.
/*
    @Test
    public void RequestWithTokenFromHelloHedvig_IS_Authorized() throws Exception {

        ResponseEntity<String> token = this.restTemplate.postForEntity("http://localhost:" + port + "/helloHedvig", "", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getBody());

        HttpEntity  entity = new HttpEntity(headers);

        assertThat(
                this.restTemplate.exchange("http://localhost:" + port + "/member/me", HttpMethod.GET, entity, String.class).getBody()
        ).contains("name");
    }

    @Test
    public void AfterLogout_RequestIsUnAuthorized() throws Exception {

        ResponseEntity<String> token = this.restTemplate.postForEntity("http://localhost:" + port + "/helloHedvig", "", String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getBody());
        HttpEntity  entity = new HttpEntity(headers);

        this.restTemplate.exchange("http://localhost:" + port + "/logout", HttpMethod.POST, entity, String.class);

        assertThat(
                this.restTemplate.exchange("http://localhost:" + port + "/member/me", HttpMethod.GET, entity, String.class)
        ).hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNAUTHORIZED);
    }*/
}