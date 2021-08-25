package com.bot.api.controllers

import com.bot.models.GuildDiscord
import com.bot.service.DiscordService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

// This controller is responsible for getting user specific data for the authed user.
@RestController
@RequestMapping("/api/me")
class MeController(private val discordService: DiscordService) {

    val objectMapper = ObjectMapper()

    @CrossOrigin(origins = ["https://toshi.kikkia.dev"], allowCredentials = "true")
    @RequestMapping("/guilds")
    fun guildsEndpoint(): ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        val response = HashMap<String, List<GuildDiscord>>()
        response["guilds"] = discordService.getGuildsForUser(auth.name)
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }
}