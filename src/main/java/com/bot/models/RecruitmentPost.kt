package com.bot.models

data class RecruitmentPost(
        val channel: String,
        val created: Long,
        val author: GuildUser)