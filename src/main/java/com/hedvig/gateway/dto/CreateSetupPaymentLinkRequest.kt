package com.hedvig.gateway.dto

import com.neovisionaries.i18n.CountryCode

data class CreateSetupPaymentLinkRequest(
  val memberId: String,
  val countryCode: CountryCode
)
