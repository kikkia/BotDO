package com.bot.api.models

import com.bot.models.BdoGuild
import com.bot.models.DiscordRole
import com.bot.models.DiscordTextChannel

data class GuildSettings(val bdoGuild: BdoGuild?,
                         val logChannel: DiscordTextChannel?,
                         val archiveChannel: DiscordTextChannel?,
                         val recruitMessage: String?,
                         val recruitRole: DiscordRole?) {}