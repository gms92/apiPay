package com.example.apiPay.application

import com.example.apiPay.BaseApplication
import com.example.apiPay.entities.Payment
import com.example.apiPay.application.commands.CreatePaymentCommand
import com.example.apiPay.entities.Card
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.infrastructure.gateway.payment.PaymentProviderGateway
import com.example.apiPay.infrastructure.gateway.payment.responses.FirstProviderStatus
import com.example.apiPay.infrastructure.gateway.payment.responses.SecondProviderStatus
import com.example.apiPay.services.PaymentProviderService
import feign.FeignException
import org.springframework.stereotype.Service
import java.util.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger{}

@Service
class CreatePaymentApplication(
    private val paymentProviderService: PaymentProviderService
): BaseApplication<CreatePaymentCommand, Payment>() {
    override val commandType = CreatePaymentCommand::class.java

    override fun execute(command: CreatePaymentCommand): Payment {
        val card = command.paymentMethod.card
        val payment = Payment(
            amount = command.amount,
            currency = command.currency,
            description = command.description,
            card = Card(
                number = card.number,
                holderName = card.holderName,
                cvv = card.cvv,
                expirationDate = card.expirationDate,
                installments = card.installments
            )
        )

        return try {
            val response = paymentProviderService.createPayment(
                payment = payment,
                provider = PaymentProvider.FIRST_PROVIDER
            )
            payment.apply {
                status = decidePaymentStatusByFirstProviderStatus(response.status)
                provider = PaymentProvider.FIRST_PROVIDER
            }
        } catch (primaryException: FeignException) {
            logger.error("Primary provider failed: ${primaryException.message}")

            try {
                val response = paymentProviderService.createPayment(
                    payment = payment,
                    provider = PaymentProvider.SECOND_PROVIDER
                )
                payment.apply {
                    status = decidePaymentStatusBySecondProviderStatus(response.status)
                    provider = PaymentProvider.SECOND_PROVIDER
                }
            } catch (secondaryException: FeignException) {
                logger.error("Secondary provider failed: ${secondaryException.message}")
                throw Exception("Both providers failed")
            }
        }
    }

    private fun decidePaymentStatusByFirstProviderStatus(status: String): PaymentStatus {
        return when (FirstProviderStatus.valueOf(status)) {
            FirstProviderStatus.AUTHORIZED -> PaymentStatus.APPROVED
            FirstProviderStatus.FAILED -> PaymentStatus.FAILED
            FirstProviderStatus.REFUNDED -> PaymentStatus.REFUNDED
        }
    }

    private fun decidePaymentStatusBySecondProviderStatus(status: String): PaymentStatus {
        return when (SecondProviderStatus.valueOf(status)) {
            SecondProviderStatus.PAID -> PaymentStatus.APPROVED
            SecondProviderStatus.FAILED -> PaymentStatus.FAILED
            SecondProviderStatus.VOIDED -> PaymentStatus.REFUNDED
        }
    }
}
