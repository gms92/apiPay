package com.example.apiPay.boundaries.controllers.payment

import com.example.apiPay.application.CreatePaymentApplication
import com.example.apiPay.application.RefundPaymentApplication
import com.example.apiPay.boundaries.controllers.payment.requests.CreatePaymentRequest
import com.example.apiPay.boundaries.controllers.payment.requests.RefundPaymentRequest
import com.example.apiPay.boundaries.controllers.payment.responses.PaymentResponse
import com.example.apiPay.enums.PaymentStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentController(
    private val createPaymentApplication: CreatePaymentApplication,
    private val refundPaymentApplication: RefundPaymentApplication,
) {
    @PostMapping("/payments")
    fun createPayment(@RequestBody request: CreatePaymentRequest): ResponseEntity<PaymentResponse> {
        val payment = createPaymentApplication.execute(request.toCommand())

        return if (payment.status == PaymentStatus.FAILED) {
            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                    PaymentResponse(messageResponse = "Erro ao processar o pagamento")
                )
        } else {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponse(payment, messageResponse = "Pagamento processado com sucesso"))
        }
    }

    @PostMapping("refunds")
    fun refundPayment(
        @RequestBody request: RefundPaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val payment = refundPaymentApplication.execute(request.toCommand())

        return if (payment == null || payment.status == PaymentStatus.FAILED) {
            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                    PaymentResponse(messageResponse = "Erro ao estornar o pagamento")
                )
        } else {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(PaymentResponse(payment, messageResponse = "Pagamento estornado com sucesso"))
        }
    }

    @GetMapping("health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.OK).body(
            "API is running"
        )
    }
}