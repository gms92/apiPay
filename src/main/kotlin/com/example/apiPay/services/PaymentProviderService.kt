package com.example.apiPay.services

import com.example.apiPay.entities.Payment
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.infrastructure.gateway.payment.dto.PaymentResponseDto

interface PaymentProviderService {
    fun createPayment(payment: Payment, provider: PaymentProvider): PaymentResponseDto

    fun refundPayment(payment: Payment, provider: PaymentProvider): PaymentResponseDto
}
