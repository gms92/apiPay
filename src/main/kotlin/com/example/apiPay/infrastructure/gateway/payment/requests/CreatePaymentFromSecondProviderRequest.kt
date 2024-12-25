package com.example.apiPay.infrastructure.gateway.payment.requests

import com.example.apiPay.enums.CurrencyISO4217
import java.math.BigDecimal

data class CreatePaymentFromSecondProviderRequest(
    val amount: BigDecimal,
    val currency: CurrencyISO4217,
    val statementDescriptor: String,
    val paymentType: String,
    val card: SecondProviderCardRequest
)

data class SecondProviderCardRequest(
    val number: String,
    val holder: String,
    val cvv: String,
    val expiration: String,
    val installmentNumber: Int
)
