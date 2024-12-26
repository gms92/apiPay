package com.example.apiPay

import com.example.apiPay.entities.Payment
import mu.KotlinLogging
import java.util.UUID

object InMemoryPaymentStore {
    private val logger = KotlinLogging.logger{}

    private val store = mutableMapOf<UUID, Payment>()

    fun save(payment: Payment) {
        store[payment.id] = payment
        logger.info { "Payment saved: $payment" }
    }

    fun findByTransactionId(transactionId: UUID): Payment? {
        logger.info { "Searching payment by transactionId: $transactionId" }
        return store.values.firstOrNull { it.transactionId == transactionId }
    }
}
