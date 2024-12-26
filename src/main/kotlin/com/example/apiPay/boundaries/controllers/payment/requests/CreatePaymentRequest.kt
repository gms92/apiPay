package com.example.apiPay.boundaries.controllers.payment.requests

import com.example.apiPay.application.commands.*
import com.example.apiPay.enums.CurrencyISO4217
import mu.KotlinLogging
import java.math.BigDecimal
import java.time.LocalDate

private val logger = KotlinLogging.logger{}

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
    init {
        require(isValidExpirationDate(expirationDate)) {
            logger.error("Invalid expiration date: $expirationDate")
        }
        require(isValidCvv(cvv)) {
            logger.error("Invalid CVV: $cvv")
        }
    }

    fun toCardInfo() = CardInfo(
        number = number,
        holderName = holderName,
        cvv = cvv,
        expirationDate = expirationDate,
        installments = installments
    )

    private fun isValidExpirationDate(date: String): Boolean {
        val pattern = "^(0[1-9]|1[0-2])/\\d{4}$".toRegex()

        if (!pattern.matches(date)) {
            return false
        }

        return try {
            val (month, year) = date.split("/")
            val expirationDate = LocalDate.of(
                year.toInt(),
                month.toInt(),
                1
            ).plusMonths(1).minusDays(1)

            expirationDate.isAfter(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidCvv(cvv: String): Boolean {
        val cvvPattern = "^\\d{3}$".toRegex()
        return cvvPattern.matches(cvv)
    }
}
