package com.bot.db.mapper

import com.bot.db.entities.UserEntity
import net.dv8tion.jda.api.entities.Member

class UserMapper {
    companion object {
        fun map(member: Member) : UserEntity {
            return UserEntity(member.user.id, member.user.name)
        }

        fun map(user: net.dv8tion.jda.api.entities.User) : UserEntity {
            return UserEntity(user.id, user.name)
        }
    }
}