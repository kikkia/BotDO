package com.bot.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import java.time.Duration


@Service
class RateLimitService {
    private val rateLimitMap: MutableMap<String, Bucket>
    init {
        rateLimitMap = ConcurrentHashMap()
    }

    fun resolveBucket(userId: String): Bucket {
        return rateLimitMap.computeIfAbsent(userId) { id: String -> newBucket(id) }
    }

    private fun newBucket(apiKey: String): Bucket {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(100, Duration.ofDays(1))))
                .build()
    }
}