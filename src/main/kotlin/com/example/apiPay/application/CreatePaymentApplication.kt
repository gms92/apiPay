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
        val attempts = listOf(
            { attemptPayment(payment, PaymentProvider.FIRST_PROVIDER, ::decidePaymentStatusByFirstProviderStatus) },
            { attemptPayment(payment, PaymentProvider.SECOND_PROVIDER, ::decidePaymentStatusBySecondProviderStatus) }
        )
        for (attempt in attempts) {
            when (val result = attempt()) {
                is PaymentAttempt.Success -> return result.payment
                is PaymentAttempt.Failure -> logger.error("Attempt failed: ${result.reason}")
            }
        }
        return payment.apply { status = PaymentStatus.FAILED }
    }

    private fun attemptPayment(
        payment: Payment,
        provider: PaymentProvider,
        statusMapper: (String) -> PaymentStatus
    ): PaymentAttempt {
        return try {
            val response = paymentProviderService.createPayment(payment, provider)
            val status = statusMapper(response.status)
            if (status == PaymentStatus.FAILED) {
                PaymentAttempt.Failure("$provider returned FAILED")
            } else {
                payment.apply {
                    this.provider = provider
                    this.status = status
                }
                PaymentAttempt.Success(payment)
            }
        } catch (ex: FeignException) {
            PaymentAttempt.Failure("${ex.message ?: "FeignException"} - $provider")
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

sealed class PaymentAttempt {
    data class Success(val payment: Payment) : PaymentAttempt()
    data class Failure(val reason: String) : PaymentAttempt()
}
