package com.example.apiPay.infrastructure.gateway.payment

import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromFirstProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromSecondProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.RefundFromFirstProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.RefundFromSecondProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.responses.FirstProviderResponse
import com.example.apiPay.infrastructure.gateway.payment.responses.SecondProviderResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.util.UUID

@FeignClient(
    name = "PaymentProviderClient",
    url = "http://localhost:9999"
)
interface PaymentProviderClient {

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/charges"],
        headers = ["Content-Type=application/json"]
    )
    fun createPaymentFromFirstProvider(
        @RequestBody request: CreatePaymentFromFirstProviderRequest
    ):  FirstProviderResponse

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/transactions"],
        headers = ["Content-Type=application/json"]
    )
    fun createPaymentFromSecondProvider(
        @RequestBody request: CreatePaymentFromSecondProviderRequest
    ): SecondProviderResponse

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/refund/{id}"],
        headers = ["Content-Type=application/json"]
    )
    fun refundPaymentFromFirstProvider(
        @PathVariable("id") id: UUID,
        @RequestBody request: RefundFromFirstProviderRequest
    ): FirstProviderResponse

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/void/{id}"],
        headers = ["Content-Type=application/json"]
    )
    fun refundPaymentFromSecondProvider(
        @PathVariable("id") id: UUID,
        @RequestBody request: RefundFromSecondProviderRequest
    ): SecondProviderResponse
}

