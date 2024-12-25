package com.example.apiPay.infrastructure.gateway.payment.dto

import java.util.UUID

data class PaymentResponseDto(
    val providerTransactionId: UUID,
    val status: String
)
