package com.example.apiPay.boundaries.controllers.payment.requests

import com.example.apiPay.application.commands.*
import com.example.apiPay.enums.CurrencyISO4217
import java.math.BigDecimal

data class CreatePaymentRequest(
    val amount: BigDecimal,
    val currency: CurrencyISO4217,
    val description: String,
    val paymentMethod: PaymentMethodRequest
) {
    fun toCommand() = CreatePaymentCommand(
        amount = amount,
        currency = currency,
        description = description,
        paymentMethod = paymentMethod.toPaymentMethod()
    )
}

data class PaymentMethodRequest(
    val type: String,
    val card: CardInfoRequest
) {
    fun toPaymentMethod() = PaymentMethod(
        type = type,
        card = card.toCardInfo()
    )
}

data class CardInfoRequest(
    val number: String,
    val holderName: String,
    val cvv: String,
    val expirationDate: String,
    val installments: Int
) {
    fun toCardInfo() = CardInfo(
        number = number,
        holderName = holderName,
        cvv = cvv,
        expirationDate = expirationDate,
        installments = installments
    )
}
