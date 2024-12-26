package com.example.apiPay.application

import com.example.apiPay.BaseApplication
import com.example.apiPay.InMemoryPaymentStore
import com.example.apiPay.entities.Payment
import com.example.apiPay.application.commands.CreatePaymentCommand
import com.example.apiPay.entities.Card
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.services.PaymentProviderService
import feign.FeignException
import org.springframework.stereotype.Service
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
        val paymentAttempts = listOf(
            { attemptPayment(payment, PaymentProvider.FIRST_PROVIDER) { PaymentStatus.fromFirstProviderStatus(it) } },
            { attemptPayment(payment, PaymentProvider.SECOND_PROVIDER) { PaymentStatus.fromSecondProviderStatus(it) } }
        )
        for (attempt in paymentAttempts) {
            when (val result = attempt()) {
                is PaymentAttempt.Success -> {
                    logger.info("Payment approved by ${result.payment.provider}")
                    InMemoryPaymentStore.save(result.payment)
                    return result.payment
                }
                is PaymentAttempt.Failure -> logger.error("Attempt failed: ${result.reason}")
            }
        }
        logger.error("All providers attempts failed")
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
                    this.transactionId = response.providerTransactionId
                    this.provider = provider
                    this.status = status
                }
                PaymentAttempt.Success(payment)
            }
        } catch (ex: FeignException) {
            PaymentAttempt.Failure("${ex.message ?: "FeignException"} - $provider")
        }
    }
}

sealed class PaymentAttempt {
    data class Success(val payment: Payment) : PaymentAttempt()
    data class Failure(val reason: String) : PaymentAttempt()
}
