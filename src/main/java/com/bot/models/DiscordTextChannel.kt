package com.bot.models

import com.bot.db.entities.TextChannelEntity

data class DiscordTextChannel(val id: String,
                             val name: String) {

    companion object {
        fun from(channel: TextChannelEntity?) : DiscordTextChannel? {
           return if (channel == null) channel
            else DiscordTextChannel(channel.id, channel.name)
        }
    }
}