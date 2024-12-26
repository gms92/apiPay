package com.example.apiPay.infrastructure.gateway.payment.requests

import java.math.BigDecimal

data class RefundFromSecondProviderRequest(
    val amount: BigDecimal
)