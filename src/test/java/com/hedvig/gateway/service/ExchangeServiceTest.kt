package com.hedvig.gateway.service

import com.hedvig.gateway.enteties.AuthorizationRow
import com.hedvig.gateway.enteties.AuthorizationRowRepository
import com.hedvig.gateway.enteties.ExchangeToken
import com.hedvig.gateway.enteties.ExchangeTokenRepository
import com.hedvig.gateway.graphql.types.ExchangeTokenResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant
import java.util.Optional

@RunWith(SpringRunner::class)
class ExchangeServiceTest {

    @MockkBean
    lateinit var authorizationRowRepository: AuthorizationRowRepository

    @MockkBean
    lateinit var exchangeTokenRepository: ExchangeTokenRepository

    @MockkBean
    lateinit var tokenService: TokenService

    lateinit var exchangeService: ExchangeService

    @Before
    fun setup() {
        exchangeService = ExchangeServiceImpl(
            authorizationRowRepository,
            exchangeTokenRepository,
            tokenService
        )

        every { authorizationRowRepository.findByMemberId(any()) } returns null
        every { authorizationRowRepository.save(any<AuthorizationRow>()) } returns null
        every { exchangeTokenRepository.save(any<ExchangeToken>()) } returns null
        every { tokenService.createPrefixedJWT(any()) } returns "token"
        every { tokenService.createJWT() } returns "token"
        every { exchangeTokenRepository.findByToken(any()) } returns null
    }

    @Test
    fun verifyThatWeSaveNewExchangeToken() {
        every { authorizationRowRepository.findByMemberId(any()) } returns AuthorizationRow()

        exchangeService.createExchangeToken("memberId")

        verify { exchangeTokenRepository.save(any<ExchangeToken>()) }
    }

    @Test
    fun verifyThatWeSaveNewAuthorizationRowToken() {
        exchangeService.createExchangeToken("memberId")

        verify { authorizationRowRepository.save(any<AuthorizationRow>()) }
        verify { exchangeTokenRepository.save(any<ExchangeToken>()) }
    }

    @Test
    fun expectThatWeRespondWithExchangeTokenInvalidResponse_WhenTheTokenIsNotPresent() {
        val sut = exchangeService.exchangeToken("token")

        assertThat(sut).hasSameClassAs(ExchangeTokenResponse.ExchangeTokenInvalidResponse)
    }

    @Test
    fun expectThatWeRespondWithExchangeTokenExpiredResponse_WhenTheTokenIsExpired() {
        every { exchangeTokenRepository.findByToken(any()) } returns ExchangeToken(
            1L, "token", "memberId", 0L
        ).also { it.createdAt = Instant.parse("1989-02-17T02:25:38Z") }

        val sut = exchangeService.exchangeToken("token")

        assertThat(sut).hasSameClassAs(ExchangeTokenResponse.ExchangeTokenExpiredResponse)
    }

    @Test
    fun expectThatWeRespondWithExchangeTokenInvalidResponse_WhenTheTokenIsMissing() {
        every { exchangeTokenRepository.findByToken(any()) } returns ExchangeToken(
            1L, "token", "memberId", 999999999L
        ).also { it.createdAt = Instant.now() }

        val sut = exchangeService.exchangeToken("token")

        assertThat(sut).hasSameClassAs(ExchangeTokenResponse.ExchangeTokenInvalidResponse)
    }

    @Test
    fun expectThatWeRespondWithExchangeTokenSuccessResponse_WhenTheTokenIAvailable() {
        every { authorizationRowRepository.findByMemberId(any()) } returns AuthorizationRow().also {
            it.token = "TOKEN"
        }
        every { exchangeTokenRepository.findByToken(any()) } returns ExchangeToken(
            1L, "token", "memberId", 999999999L
        ).also { it.createdAt = Instant.now() }

        val sut = exchangeService.exchangeToken("token")

        assertThat((sut as ExchangeTokenResponse.ExchangeTokenSuccessResponse).token).isEqualTo("TOKEN")
    }
}