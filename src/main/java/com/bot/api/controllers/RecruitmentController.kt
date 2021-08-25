package com.bot.api.controllers

import com.bot.exceptions.api.InsufficientPermissionException
import com.bot.exceptions.api.InvalidParamException
import com.bot.api.models.RecruitmentPost
import com.bot.service.DiscordService
import com.bot.service.GuildService
import com.bot.service.RecruitmentPostService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/api/{guildId}/recruitment")
class RecruitmentController(private val recruitmentPostService: RecruitmentPostService,
                            private val discordService: DiscordService,
                            private val guildService: GuildService) {
    val objectMapper = ObjectMapper()

    @RequestMapping("/posts")
    fun recruitPostEndpoint(@RequestParam daysAgo: Int, @PathVariable("guildId") guildId : String): ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        if (!discordService.canUserAdminGuild(auth.name, guildId)) {
            throw InsufficientPermissionException("You do not have permission to view this guilds info.")
        }

        if (daysAgo < 1 || daysAgo > 365) {
            throw InvalidParamException("Days ago needs to be between 1 and 365, Mr. Hacker you.")
        }

        val guildEntity = guildService.getById(guildId)
        val afterTime = Instant.now().minus(daysAgo.toLong(), ChronoUnit.DAYS)

        val response = HashMap<String, List<RecruitmentPost>>()
        response["posts"] = recruitmentPostService.getAllForGuildInPast(guildEntity, Timestamp.from(afterTime))
        return ResponseEntity.ok(objectMapper.writeValueAsString(response))
    }
}