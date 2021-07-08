package com.bot.db.mapper

import com.bot.db.entities.UserEntity
import com.bot.models.GuildUser

class GuildUserMapper {
    companion object {
        fun map(user: UserEntity) : GuildUser {
            return GuildUser(user.id, user.name, user.familyName, user.avatar)
        }
    }
}