package com.bot.api.controllers

import com.bot.exceptions.api.InsufficientPermissionException
import com.bot.models.BdoGuild
import com.bot.models.DiscordRole
import com.bot.models.DiscordTextChannel
import com.bot.api.models.GuildSettings
import com.bot.service.DiscordService
import com.bot.service.GuildService
import com.bot.service.TextChannelService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/{guildId}/settings")
class SettingsController(private val discordService: DiscordService,
                        private val guildService: GuildService,
                        private val textChannelService: TextChannelService) {
    val objectMapper = ObjectMapper()

    @GetMapping("/")
    fun getSettingsEndpoint(@PathVariable("guildId") guildId: String) : ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
        if (!discordService.canUserAdminGuild(auth.name, guildId)) {
            throw InsufficientPermissionException("You do not have permission to view this guilds info.")
        }

        val guildEntity = guildService.getById(guildId)
        val logChannel = if (guildEntity.logChannel == null) null
            else textChannelService.getById(guildEntity.logChannel)
        val archiveChannel = if (guildEntity.archiveChannel == null) null
            else textChannelService.getById(guildEntity.archiveChannel)
        val recruitRole = if (guildEntity.recruitRole == null) null
            else discordService.getRoleInGuild(guildId, guildEntity.recruitRole!!)

        return ResponseEntity.ok(objectMapper.writeValueAsString(
                GuildSettings(BdoGuild.from(guildEntity.bdoGuild),
                DiscordTextChannel.from(logChannel),
                DiscordTextChannel.from(archiveChannel),
                guildEntity.recruitMessage,
                DiscordRole.from(recruitRole)
                )
        ))
    }

    @PostMapping("/")
    fun postSettingsEndpoint(@RequestBody body: GuildSettings, @PathVariable("guildId") guildId: String) {
        val auth = SecurityContextHolder.getContext().authentication
        if (!discordService.canUserAdminGuild(auth.name, guildId)) {
            throw InsufficientPermissionException("You do not have permission to view this guilds info.")
        }
        val guildEntity = guildService.getById(guildId)

    }
}