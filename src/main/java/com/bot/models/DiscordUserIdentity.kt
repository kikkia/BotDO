package com.bot.models

data class DiscordUserIdentity(val userId: String, val username: String, val discriminator: Int, val avatar: String) {
}