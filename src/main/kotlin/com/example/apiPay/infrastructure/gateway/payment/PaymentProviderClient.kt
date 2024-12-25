package com.example.apiPay.infrastructure.gateway.payment

import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromFirstProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.requests.CreatePaymentFromSecondProviderRequest
import com.example.apiPay.infrastructure.gateway.payment.responses.CreatePaymentFromFirstProviderResponse
import com.example.apiPay.infrastructure.gateway.payment.responses.CreatePaymentFromSecondProviderResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

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
    ): CreatePaymentFromFirstProviderResponse

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/transactions"],
        headers = ["Content-Type=application/json"]
    )
    fun createPaymentFromSecondProvider(
        @RequestBody request: CreatePaymentFromSecondProviderRequest
    ): CreatePaymentFromSecondProviderResponse

}

