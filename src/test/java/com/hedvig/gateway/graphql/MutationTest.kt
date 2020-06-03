package com.hedvig.gateway.graphql

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import com.hedvig.gateway.service.ExchangeService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MutationTest {

  @MockkBean
  lateinit var exchangeService: ExchangeService

  @Autowired
  private lateinit var graphQLTestTemplate: GraphQLTestTemplate

  @Test
  fun test_ExchangeTokenSuccessResponse() {
    every { exchangeService.exchangeToken(any<String>()) } returns ExchangeTokenResponse.ExchangeTokenSuccessResponse("TokenOutput")

    val response = graphQLTestTemplate.perform("/mutations/exchangeTokenSuccess.graphql", null)


    assertThat(response.isOk).isTrue()
    assertThat(response.toString().contains("TokenOutput"))
  }

  @Test
  fun test_ExchangeTokenInvalidResponse() {
    every { exchangeService.exchangeToken(any<String>()) } returns ExchangeTokenResponse.ExchangeTokenInvalidResponse

    val response = graphQLTestTemplate.perform("/mutations/exchangeTokenInvalid.graphql", null)

    assertThat(response.isOk).isTrue()
  }

  @Test
  fun test_ExchangeTokenExpiredResponse() {
    every { exchangeService.exchangeToken(any<String>()) } returns ExchangeTokenResponse.ExchangeTokenExpiredResponse

    val response = graphQLTestTemplate.perform("/mutations/exchangeTokenExpired.graphql", null)

    assertThat(response.isOk).isTrue()
  }
}