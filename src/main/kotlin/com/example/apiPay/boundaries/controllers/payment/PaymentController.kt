package com.example.apiPay.boundaries.controllers.payment

import com.example.apiPay.application.CreatePaymentApplication
import com.example.apiPay.boundaries.controllers.payment.requests.CreatePaymentRequest
import com.example.apiPay.boundaries.controllers.payment.responses.CreatePaymentResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    private val createPaymentApplication: CreatePaymentApplication,
) {
    @PostMapping("payments")
    fun createPayment(
        @RequestBody request: CreatePaymentRequest
    ): ResponseEntity<CreatePaymentResponse> {
        val payment = createPaymentApplication.execute(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(
            CreatePaymentResponse(payment)
        )
    }

    @GetMapping("health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(
            "API is running"
        )
    }
}