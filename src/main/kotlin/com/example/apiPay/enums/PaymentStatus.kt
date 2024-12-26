package com.example.apiPay.enums

import com.example.apiPay.infrastructure.gateway.payment.responses.FirstProviderStatus
import com.example.apiPay.infrastructure.gateway.payment.responses.SecondProviderStatus

enum class PaymentStatus {
    PENDING,
    APPROVED,
    FAILED,
    REFUNDED;

    companion object {
        fun fromFirstProviderStatus(status: String): PaymentStatus {
            return when (FirstProviderStatus.valueOf(status.uppercase())) {
                FirstProviderStatus.AUTHORIZED -> APPROVED
                FirstProviderStatus.FAILED     -> FAILED
                FirstProviderStatus.REFUNDED   -> REFUNDED
            }
        }

        fun fromSecondProviderStatus(status: String): PaymentStatus {
            return when (SecondProviderStatus.valueOf(status.uppercase())) {
                SecondProviderStatus.PAID   -> APPROVED
                SecondProviderStatus.FAILED -> FAILED
                SecondProviderStatus.VOIDED -> REFUNDED
            }
        }
    }
}