package com.example.apiPay.infrastructure.gateway.payment

import com.example.apiPay.entities.Payment
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.infrastructure.gateway.payment.dto.PaymentResponseDto
import com.example.apiPay.infrastructure.gateway.payment.requests.RefundFromFirstProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.RefundFromSecondProviderRequest
import com.example.apiPay.services.PaymentProviderService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger{}

@Service
class PaymentProviderGateway(
    private val paymentProviderClient: PaymentProviderClient,
) : PaymentProviderService {
    override fun createPayment(payment: Payment, provider: PaymentProvider): PaymentResponseDto {
        return when (provider) {
            PaymentProvider.FIRST_PROVIDER -> {
                val request = payment.toFirstProviderRequest()
                val response = paymentProviderClient.createPaymentFromFirstProvider(request)
                logger.info("Received create payment response from FIRST_PROVIDER: $response")
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
            PaymentProvider.SECOND_PROVIDER -> {
                val request = payment.toSecondProviderRequest()
                val response = paymentProviderClient.createPaymentFromSecondProvider(request)
                logger.info("Received create payment response from SECOND_PROVIDER: $response")
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
        }
    }

    override fun refundPayment(payment: Payment, provider: PaymentProvider): PaymentResponseDto {
        return when (provider) {
            PaymentProvider.FIRST_PROVIDER -> {
                val request = RefundFromFirstProviderRequest(payment.amount)
                val response = paymentProviderClient.refundPaymentFromFirstProvider(payment.transactionId, request)
                logger.info("Received refund response from FIRST_PROVIDER: $response")
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
            PaymentProvider.SECOND_PROVIDER -> {
                val request = RefundFromSecondProviderRequest(payment.amount)
                val response = paymentProviderClient.refundPaymentFromSecondProvider(payment.transactionId, request)
                logger.info("Received refund response from SECOND_PROVIDER: $response")
                PaymentResponseDto(
                    providerTransactionId = response.id,
                    status = response.status.name
                )
            }
        }
    }
}