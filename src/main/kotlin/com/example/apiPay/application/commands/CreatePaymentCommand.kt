package com.example.apiPay.application.commands

import com.example.apiPay.BaseCommand
import com.example.apiPay.enums.CurrencyISO4217
import java.math.BigDecimal

data class CreatePaymentCommand(
    val amount: BigDecimal,
    val currency: CurrencyISO4217,
    val description: String,
    val paymentMethod: PaymentMethod
) : BaseCommand

data class PaymentMethod(
    val type: String,
    val card: CardInfo
)

data class CardInfo(
    val number: String,
    val holderName: String,
    val cvv: String,
    val expirationDate: String,
    val installments: Int = 1
)
