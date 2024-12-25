package com.example.apiPay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class ApiPayApp

fun main(args: Array<String>) {
	runApplication<ApiPayApp>(*args)
}
