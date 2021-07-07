package com.bot.db.mapper

import com.bot.db.entities.GuildEntity
import com.bot.models.GuildDiscord

class GuildDiscordMapper {
    companion object {
        fun map(guildEntity: GuildEntity, guildIcon: String? = null) : GuildDiscord {
            return GuildDiscord(guildEntity.id, guildEntity.name, guildEntity.bdoGuild!!.name, guildIcon)
        }
    }
}