package com.bot.api.models

import com.bot.models.GuildUser

data class RecruitmentPost(
        val channel: String,
        val created: Long,
        val author: GuildUser
)