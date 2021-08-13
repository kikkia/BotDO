package com.bot.models

import net.dv8tion.jda.api.entities.Role

data class DiscordRole(val id: String,
                 val name: String) {

    companion object {
        fun from(role: Role?) : DiscordRole? {
            return if (role == null) role else DiscordRole(role.id, role.name)
        }
    }
}