package com.bot.api.controllers

import com.bot.api.exceptions.FamilyNotFoundException
import com.bot.api.exceptions.RegionNotFoundException
import com.bot.models.Region
import com.bot.service.FamilyService
import com.bot.service.RateLimitService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import java.sql.Timestamp
import java.time.Instant

@RestController
@RequestMapping("/api/lookup")
class LookupController(private val familyService: FamilyService, private val rateLimitService: RateLimitService) {
    val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/user")
    fun userLookup(@RequestParam familyName: String, @RequestParam("region") regionCode : String) : ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication

        log.info("User ${auth.principal} requested family lookup for $regionCode - $familyName")

        val bucket = rateLimitService.resolveBucket(auth.principal.toString())
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            val region = Region.getByCode(regionCode) ?: throw RegionNotFoundException("Region not found")
            val familyOpt = familyService.getFamily(familyName, region, true)
            if (familyOpt.isEmpty) {
                throw FamilyNotFoundException("Family not found on region")
            }
            // Do not return fields of no use to consumers
            val family = familyOpt.get()
            family.externalId = ""
            family.lastUpdated = Timestamp.from(Instant.now())

            return ResponseEntity
                    .ok()
                    .header("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
                    .body(objectMapper.writeValueAsString(family))
        } else {
            val waitForRefill = probe.nanosToWaitForRefill / 1000000000
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
                    .build()
        }
    }
}