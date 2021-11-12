package com.bot.api.controllers

import com.bot.api.exceptions.FamilyNotFoundException
import com.bot.api.exceptions.RegionNotFoundException
import com.bot.models.Region
import com.bot.service.FamilyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lookup")
class LookupController(private val familyService: FamilyService) {
    val objectMapper = ObjectMapper()

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/user")
    fun userLookup(@RequestParam familyName: String, @RequestParam("region") regionCode : String) : ResponseEntity<String> {
        val region = Region.getByCode(regionCode) ?: throw RegionNotFoundException("Region not found")
        val family = familyService.getFamily(familyName, region, true)
        if (family.isEmpty) {
            throw FamilyNotFoundException("Family not found on region")
        }
        return ResponseEntity.ok(objectMapper.writeValueAsString(family.get()))
    }
}