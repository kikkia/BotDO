package com.bot.db.mapper

import com.bot.db.entities.User
import net.dv8tion.jda.api.entities.Member

class UserMapper {
    companion object {
        fun map(member: Member) : User {
            return User(member.user.id, member.user.name, member.user.name)
        }

        fun map(user: net.dv8tion.jda.api.entities.User) : User {
            return User(user.id, user.name, user.name)
        }
    }
}