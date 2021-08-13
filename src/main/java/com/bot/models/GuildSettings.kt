package com.bot.models

data class GuildSettings(val bdoGuild: BdoGuild?,
                        val logChannel: DiscordTextChannel?,
                        val archiveChannel: DiscordTextChannel?,
                        val recruitMessage: String?,
                        val recruitRole: DiscordRole?) {}