package com.bot.api.controllers

import com.bot.api.exceptions.FamilyNotFoundException
import com.bot.api.exceptions.RegionNotFoundException
import com.bot.models.Region
import com.bot.service.FamilyService
import com.bot.service.RateLimitService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/lookup")
class LookupController(private val familyService: FamilyService, private val rateLimitService: RateLimitService) {
    val objectMapper = ObjectMapper()

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/user")
    fun userLookup(@RequestParam familyName: String, @RequestParam("region") regionCode : String) : ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication

        val bucket = rateLimitService.resolveBucket(auth.principal.toString())
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            val region = Region.getByCode(regionCode) ?: throw RegionNotFoundException("Region not found")
            val family = familyService.getFamily(familyName, region, true)
            if (family.isEmpty) {
                throw FamilyNotFoundException("Family not found on region")
            }
            return ResponseEntity
                    .ok()
                    .header("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
                    .body(objectMapper.writeValueAsString(family.get()))
        } else {
            val waitForRefill = probe.nanosToWaitForRefill / 1000000000
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
                    .build()
        }
    }
}