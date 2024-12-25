package com.example.apiPay.infrastructure.gateway.payment.requests

import com.example.apiPay.enums.CurrencyISO4217
import java.math.BigDecimal

data class CreatePaymentFromFirstProviderRequest(
    val amount: BigDecimal,
    val currency: CurrencyISO4217,
    val description: String,
    val paymentMethod: FirstProviderPaymentMethodRequest
)

data class FirstProviderPaymentMethodRequest(
    val type: String,
    val card: FirstProviderCardRequest
)

data class FirstProviderCardRequest(
    val number: String,
    val holderName: String,
    val cvv: String,
    val expirationDate: String,
    val installments: Int
)
