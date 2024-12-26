package com.example.apiPay.application.commands

import com.example.apiPay.BaseCommand
import java.math.BigDecimal
import java.util.UUID

data class RefundPaymentCommand(
    val transactionId: UUID,
    val amount: BigDecimal
): BaseCommand