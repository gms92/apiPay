package com.example.apiPay.boundaries.controllers.payment.responses

import com.example.apiPay.entities.Payment

data class CreatePaymentResponse(private val payment: Payment) {
    val paymentId = payment.id
    val status = payment.status
    val provider = payment.provider
}