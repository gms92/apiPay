package com.example.apiPay.boundaries.controllers.payment.responses

import com.example.apiPay.entities.Payment

data class PaymentResponse(
    private val payment: Payment? = null,
    private val messageResponse: String? = null
) {
    val transactionId = payment?.transactionId
    val provider = payment?.provider
    val message = messageResponse
}