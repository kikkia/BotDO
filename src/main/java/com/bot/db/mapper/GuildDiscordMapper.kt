package com.bot.db.mapper

import com.bot.db.entities.GuildEntity
import com.bot.models.GuildDiscord

class GuildDiscordMapper {
    companion object {
        fun map(guildEntity: GuildEntity) : GuildDiscord {
            return GuildDiscord(guildEntity.id, guildEntity.name, guildEntity.bdoGuild!!.name, guildEntity.avatar)
        }
    }
}