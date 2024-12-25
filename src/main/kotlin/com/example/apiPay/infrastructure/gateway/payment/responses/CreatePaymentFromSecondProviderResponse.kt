package com.example.apiPay.infrastructure.gateway.payment.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.UUID

data class CreatePaymentFromSecondProviderResponse(
    val id: UUID,
    val date: String,
    val status: SecondProviderStatus,
    val amount: BigDecimal,
    val originalAmount: BigDecimal,
    val currency: String,
    val statementDescriptor: String,
    val paymentType: String,
    val cardId: UUID
)

enum class SecondProviderStatus {
    @JsonProperty("paid")
    PAID,

    @JsonProperty("failed")
    FAILED,

    @JsonProperty("voided")
    VOIDED
}