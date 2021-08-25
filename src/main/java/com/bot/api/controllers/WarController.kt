package com.bot.api.controllers

import com.bot.api.models.RecruitmentPost
import com.bot.api.models.War
import com.bot.api.models.mappers.WarMapper
import com.bot.exceptions.api.InsufficientPermissionException
import com.bot.exceptions.api.WarsNotSetupException
import com.bot.service.DiscordService
import com.bot.service.GuildService
import com.bot.service.WarService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/{guildId}/war")
class WarController(private val discordService: DiscordService,
                    private val guildService: GuildService,
                    private val warService: WarService) {
    val objectMapper = ObjectMapper()

    @RequestMapping("/history")
    fun getPastWars(@RequestParam daysAgo: Int, @PathVariable("guildId") guildId: String) : ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        if (!discordService.canUserAdminGuild(auth.name, guildId)) {
            throw InsufficientPermissionException("You do not have permission to view this guilds info.")
        }
        val guildEntity = guildService.getById(guildId)
        if (guildEntity.bdoGuild == null) {
            throw WarsNotSetupException("Wars are not setup for this guild")
        }

        val response = HashMap<String, List<War>>()
        response["wars"] = warService.getPastByGuildAfter(guildEntity.bdoGuild!!.id,
            Instant.now().minus(daysAgo.toLong(), ChronoUnit.DAYS))
            .stream()
            .map { WarMapper.map(it) }
            .collect(
            Collectors.toList())
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }

    @RequestMapping("/upcoming")
    fun getUpcomingWars(@PathVariable("guildId") guildId: String) : ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        if (!discordService.canUserAdminGuild(auth.name, guildId)) {
            throw InsufficientPermissionException("You do not have permission to view this guilds info.")
        }
        val guildEntity = guildService.getById(guildId)
        if (guildEntity.bdoGuild == null) {
            throw WarsNotSetupException("Wars are not setup for this guild")
        }

        val response = HashMap<String, List<War>>()
        response["wars"] = warService.getAllUpcomingByGuildId(guildEntity.bdoGuild!!.id)
            .stream()
            .map { WarMapper.map(it) }
            .collect(
                Collectors.toList())
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }
}