package com.example.apiPay.infrastructure.gateway.payment

import com.example.apiPay.entities.Payment
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.infrastructure.gateway.payment.dto.PaymentResponseDto
import com.example.apiPay.services.PaymentProviderService
import org.springframework.stereotype.Service

@Service
class PaymentProviderGateway(
    private val paymentProviderClient: PaymentProviderClient
) : PaymentProviderService {
    override fun createPayment(payment: Payment, provider: PaymentProvider): PaymentResponseDto {
        return when (provider) {
            PaymentProvider.FIRST_PROVIDER -> {
                val request = payment.toFirstProviderRequest()
                val response = paymentProviderClient.createPaymentFromFirstProvider(request)
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
            PaymentProvider.SECOND_PROVIDER -> {
                val request = payment.toSecondProviderRequest()
                val response = paymentProviderClient.createPaymentFromSecondProvider(request)
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
        }
    }
}