package com.example.apiPay.infrastructure.gateway.payment.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.UUID

data class FirstProviderResponse(
    val id: UUID,
    val createdAt: String,
    val status: FirstProviderStatus,
    val originalAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val currency: String,
    val description: String,
    val paymentMethod: String,
    val cardId: UUID
)

enum class FirstProviderStatus {
    @JsonProperty("authorized")
    AUTHORIZED,

    @JsonProperty("failed")
    FAILED,

    @JsonProperty("refunded")
    REFUNDED
}