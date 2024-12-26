package com.example.apiPay.boundaries.controllers.payment.requests

import com.example.apiPay.application.commands.RefundPaymentCommand
import java.math.BigDecimal
import java.util.UUID

data class RefundPaymentRequest(
    val transactionId: UUID,
    val amount: BigDecimal? = null
) {
    fun toCommand() = RefundPaymentCommand(
        transactionId = transactionId,
        amount = amount
    )
}