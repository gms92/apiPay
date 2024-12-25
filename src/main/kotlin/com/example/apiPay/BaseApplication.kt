package com.example.apiPay

import feign.FeignException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
abstract class BaseApplication<Command : BaseCommand, Response> {
    protected abstract val commandType: Class<*>

    @Retryable(
        value = [FeignException::class],
        backoff = Backoff(
            random = true,
            multiplier = 1.2,
            delay = 100,
            maxDelay = 3000
        ),
        maxAttempts = 5
    )
    abstract fun execute(command: Command): Response
}
