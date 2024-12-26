package com.example.apiPay.application

import com.example.apiPay.InMemoryPaymentStore
import com.example.apiPay.application.commands.RefundPaymentCommand
import com.example.apiPay.entities.Card
import com.example.apiPay.entities.Payment
import com.example.apiPay.enums.CurrencyISO4217
import com.example.apiPay.enums.PaymentMethodType
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.infrastructure.gateway.payment.dto.PaymentResponseDto
import com.example.apiPay.services.PaymentProviderService
import feign.FeignException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RefundPaymentApplicationTest {

    private val paymentProviderService = mockk<PaymentProviderService>()
    private val refundPaymentApplication = RefundPaymentApplication(paymentProviderService)

    @BeforeEach
    fun setUp() {
        mockkObject(InMemoryPaymentStore)
    }

    @Test
    fun `should set payment status to REFUNDED if respective provider returns REFUNDED`() {
        // Arrange
        val command = buildRefundCommand()
        val payment = buildPayment().apply {
            provider = PaymentProvider.FIRST_PROVIDER
            status = PaymentStatus.APPROVED
        }
        every { InMemoryPaymentStore.findByTransactionId(command.transactionId) } returns payment
        every { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER) } returns
                PaymentResponseDto(UUID.randomUUID(), "REFUNDED")

        // Act
        val result = refundPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.REFUNDED, result?.status)
        assertEquals(PaymentProvider.FIRST_PROVIDER, result?.provider)
        verify(exactly = 1) { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER) }
    }

    @Test
    fun `should set payment status to FAILED if respective provider returns FAILED`() {
        // Arrange
        val command = buildRefundCommand()
        val payment = buildPayment().apply {
            provider = PaymentProvider.FIRST_PROVIDER
            status = PaymentStatus.APPROVED
        }
        every { InMemoryPaymentStore.findByTransactionId(command.transactionId) } returns payment
        every { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER) } returns
                PaymentResponseDto(UUID.randomUUID(), "FAILED")

        // Act
        val result = refundPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.FAILED, result?.status)
        assertEquals(PaymentProvider.FIRST_PROVIDER, result?.provider)
        verify(exactly = 1) { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER) }
    }

    @Test
    fun `should set payment status to FAILED if FeignException is thrown`() {
        // Arrange
        val command = buildRefundCommand()
        val payment = buildPayment().apply {
            provider = PaymentProvider.FIRST_PROVIDER
            status = PaymentStatus.APPROVED
        }
        val exception = mockk<FeignException>()
        every { exception.message } returns "Refund error"
        every { InMemoryPaymentStore.findByTransactionId(command.transactionId) } returns payment
        every { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER)
            } throws exception

        // Act
        val result = refundPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.FAILED, result?.status)
        assertEquals(PaymentProvider.FIRST_PROVIDER, result?.provider)
        verify(exactly = 1) { paymentProviderService.refundPayment(payment, PaymentProvider.FIRST_PROVIDER) }
    }

    private fun buildRefundCommand(): RefundPaymentCommand {
        return RefundPaymentCommand(
            transactionId = UUID.randomUUID(),
            amount = 250.toBigDecimal()
        )
    }

    private fun buildPayment(): Payment {
        return Payment(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            amount = 250.toBigDecimal(),
            currency = CurrencyISO4217.BRL,
            description = "Test payment",
            methodType = PaymentMethodType.CARD,
            card = Card(
                number = "1111222233334444",
                holderName = "Foo bar",
                cvv = "123",
                expirationDate = "12/2030",
                installments = 1
            ),
            status = PaymentStatus.APPROVED,
            provider = null
        )
    }
}