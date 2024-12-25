package com.example.apiPay.entities

import com.example.apiPay.enums.CurrencyISO4217
import com.example.apiPay.enums.PaymentMethodType
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromFirstProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromSecondProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.FirstProviderCardRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.FirstProviderPaymentMethodRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.SecondProviderCardRequest
import java.math.BigDecimal
import java.util.*

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val amount: BigDecimal,
    val currency: CurrencyISO4217,
    val description: String,
    val methodType: PaymentMethodType = PaymentMethodType.CARD,
    val card: Card? = null,
    var status: PaymentStatus = PaymentStatus.PENDING,
    var provider: PaymentProvider? = null
) {
    fun toFirstProviderRequest(): CreatePaymentFromFirstProviderRequest {
        return CreatePaymentFromFirstProviderRequest(
            amount = this.amount,
            currency = this.currency,
            description = this.description,
            paymentMethod = FirstProviderPaymentMethodRequest(
                type = "card",
                card = FirstProviderCardRequest(
                    number = this.card?.number ?: "",
                    holderName = this.card?.holderName ?: "",
                    cvv = this.card?.cvv ?: "",
                    expirationDate = this.card?.expirationDate ?: "",
                    installments = this.card?.installments ?: 1
                )
            )
        )
    }

    fun toSecondProviderRequest(): CreatePaymentFromSecondProviderRequest {
        return CreatePaymentFromSecondProviderRequest(
            amount = this.amount,
            currency = this.currency,
            statementDescriptor = this.description,
            paymentType = "card",
            card = SecondProviderCardRequest(
                number = this.card?.number ?: "",
                holder = this.card?.holderName ?: "",
                cvv = this.card?.cvv ?: "",
                expiration = this.card?.expirationDate ?: "",
                installmentNumber = this.card?.installments ?: 1
            )
        )
    }
}

data class Card(
    val number: String,
    val holderName: String,
    val cvv: String,
    val expirationDate: String,
    val installments: Int
)


