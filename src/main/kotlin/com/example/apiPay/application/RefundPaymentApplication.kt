package com.example.apiPay.application

import com.example.apiPay.BaseApplication
import com.example.apiPay.InMemoryPaymentStore
import com.example.apiPay.application.commands.RefundPaymentCommand
import com.example.apiPay.entities.Payment
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.services.PaymentProviderService
import feign.FeignException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

private val logger = KotlinLogging.logger{}

@Service
class RefundPaymentApplication(
    private val paymentProviderService: PaymentProviderService
) : BaseApplication<RefundPaymentCommand, Payment?>() {

    override val commandType = RefundPaymentCommand::class.java

    override fun execute(command: RefundPaymentCommand): Payment? {
        val payment = findPayment(command.transactionId)
            ?: return null.also { logger.error("Payment not found") }

        return try {
            val response = paymentProviderService.refundPayment(payment, payment.provider!!)
            val refundStatus = when (payment.provider!!) {
                PaymentProvider.FIRST_PROVIDER  -> PaymentStatus.fromFirstProviderStatus(response.status)
                PaymentProvider.SECOND_PROVIDER -> PaymentStatus.fromSecondProviderStatus(response.status)
            }

            if (refundStatus == PaymentStatus.REFUNDED) {
                payment.apply { status = PaymentStatus.REFUNDED }
            } else {
                payment.apply { status = PaymentStatus.FAILED }
            }
        } catch (ex: FeignException) {
            logger.error("Refund failed on ${payment.provider}: ${ex.message}")
            payment.status = PaymentStatus.FAILED
            payment
        }
    }

    private fun findPayment(transactionId: UUID): Payment? {
        return InMemoryPaymentStore.findByTransactionId(transactionId)
    }
}
