package com.example.apiPay.application

import com.example.apiPay.application.commands.CardInfo
import com.example.apiPay.application.commands.CreatePaymentCommand
import com.example.apiPay.application.commands.PaymentMethod
import com.example.apiPay.enums.CurrencyISO4217
import com.example.apiPay.enums.PaymentProvider
import com.example.apiPay.enums.PaymentStatus
import com.example.apiPay.infrastructure.gateway.payment.dto.PaymentResponseDto
import com.example.apiPay.services.PaymentProviderService
import feign.FeignException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class CreatePaymentApplicationTest {
    private val paymentProviderService = mockk<PaymentProviderService>()
    private val createPaymentApplication = CreatePaymentApplication(paymentProviderService)

    @Test
    fun `should approve payment when FIRST_PROVIDER returns AUTHORIZED`() {
        // Arrange
        val command = buildCommand()
        every { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER)
            } returns PaymentResponseDto(UUID.randomUUID(),"AUTHORIZED")

        // Act
        val payment = createPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.APPROVED, payment.status)
        assertEquals(PaymentProvider.FIRST_PROVIDER, payment.provider)
        verify(exactly = 0) { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) }
    }

    @Test
    fun `should call SECOND_PROVIDER and approve when FIRST_PROVIDER throws FeignException`() {
        // Arrange
        val exception = mockk<FeignException>()
        val command = buildCommand()
        every { exception.message } returns "Provider error"
        every { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER)
            } throws exception
        every { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER)
            } returns PaymentResponseDto(UUID.randomUUID(),"PAID")

        // Act
        val payment = createPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.APPROVED, payment.status)
        assertEquals(PaymentProvider.SECOND_PROVIDER, payment.provider)
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) }
    }

    @Test
    fun `should call SECOND_PROVIDER and approve when FIRST_PROVIDER returns failed`() {
        // Arrange
        val command = buildCommand()
        every { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER)
        } returns PaymentResponseDto(UUID.randomUUID(),"FAILED")
        every { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER)
        } returns PaymentResponseDto(UUID.randomUUID(),"PAID")

        // Act
        val payment = createPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.APPROVED, payment.status)
        assertEquals(PaymentProvider.SECOND_PROVIDER, payment.provider)
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) }
    }

    @Test
    fun `should set payment status to FAILED if both providers throw FeignException`() {
        // Arrange
        val exception = mockk<FeignException>()
        val command = buildCommand()
        every { exception.message } returns "Provider error"
        every { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER) } throws exception
        every { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) } throws exception

        // Act
        val payment = createPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.FAILED, payment.status)
        assertEquals(null, payment.provider)
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER) }
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) }
    }

    @Test
    fun `should set payment status to FAILED if both providers return FAILED status`() {
        // Arrange
        val command = buildCommand()
        every { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER)
            } returns PaymentResponseDto(UUID.randomUUID(), "FAILED")
        every { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER)
            } returns PaymentResponseDto(UUID.randomUUID(), "FAILED")

        // Act
        val payment = createPaymentApplication.execute(command)

        // Assert
        assertEquals(PaymentStatus.FAILED, payment.status)
        assertEquals(null, payment.provider)
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.FIRST_PROVIDER) }
        verify(exactly = 1) { paymentProviderService.createPayment(any(), PaymentProvider.SECOND_PROVIDER) }
    }

    private fun buildCommand(): CreatePaymentCommand {
        return CreatePaymentCommand(
            amount = 250.toBigDecimal(),
            currency = CurrencyISO4217.BRL,
            description = "Test payment",
            paymentMethod = PaymentMethod(
                type = "card",
                card = CardInfo(
                    number = "1111222233334444",
                    holderName = "Foo bar",
                    cvv = "123",
                    expirationDate = "12/2030",
                    installments = 1
                )
            )
        )
    }
}