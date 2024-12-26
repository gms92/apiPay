package com.example.apiPay.infrastructure.gateway.payment.requests

import java.math.BigDecimal

data class RefundFromFirstProviderRequest(
    val amount: BigDecimal
)