package com.example.apiPay

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component

@Component
class PaymentProviderMock {

    private val wireMockServer = WireMockServer(
        WireMockConfiguration().port(9999)
    )

    @PostConstruct
    fun startMockServer() {
        wireMockServer.start()

        //Provider 1
        wireMockServer.stubFor(
            post(urlEqualTo("/charges"))
                .withRequestBody(matchingJsonPath("$.description", containing("This will fail")))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "id": "55555555-ffff-4444-aaaa-000000000000",
                              "createdAt": "2024-12-23",
                              "status": "failed",
                              "originalAmount": 1000,
                              "currentAmount": 0,
                              "currency": "USD",
                              "description": "this will fail",
                              "paymentMethod": "card",
                              "cardId": "11111111-2222-3333-4444-555555555555"
                            }
                            """.trimIndent()
                        )
                )
                .atPriority(1)
        )

        wireMockServer.stubFor(
            post(urlEqualTo("/charges"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "id": "3af5f7fa-cc91-4af4-b0cd-7d8da6e2d5d0",
                              "createdAt": "2024-12-23",
                              "status": "authorized",
                              "originalAmount": 1000,
                              "currentAmount": 1000,
                              "currency": "USD",
                              "description": "Payment for order #12345",
                              "paymentMethod": "card",
                              "cardId": "7aa486c6-bc08-4643-9deb-21ecdf1907ba"
                            }
                            """.trimIndent()
                        )
                )
        )

        //Provider 2
        wireMockServer.stubFor(
            post(urlEqualTo("/transactions"))
                .withRequestBody(
                    matchingJsonPath("$.statementDescriptor", containing("This will fail"))
                )
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "id": "55555555-ffff-4444-aaaa-000000000000",
                              "date": "2024-12-23",
                              "status": "failed",
                              "amount": 1000,
                              "originalAmount": 1000,
                              "currency": "USD",
                              "statementDescriptor": "fail transaction",
                              "paymentType": "card",
                              "cardId": "99999999-8888-7777-6666-555555555555"
                            }
                            """.trimIndent()
                        )
                )
                .atPriority(1)
        )

        wireMockServer.stubFor(
            post(urlEqualTo("/transactions"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "id": "fc6e6d09-7f9c-4ca0-9f99-32826a204919",
                              "date": "2024-12-23",
                              "status": "paid",
                              "amount": 1000,
                              "originalAmount": 1000,
                              "currency": "USD",
                              "statementDescriptor": "Payment for order #12345",
                              "paymentType": "card",
                              "cardId": "580094a0-6c64-4c14-a578-ab0b2ae7d751"
                            }
                            """.trimIndent()
                        )
                )
        )
    }

    @PreDestroy
    fun stopMockServer() {
        wireMockServer.stop()
    }
}
