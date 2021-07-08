package com.bot.db.mapper

import com.bot.db.entities.RecruitmentPostEntity
import com.bot.models.RecruitmentPost

class RecruitmentPostMapper {
    companion object {
        fun map(recruitPost: RecruitmentPostEntity) : RecruitmentPost {
            return RecruitmentPost(recruitPost.channel, recruitPost.timestamp.time, GuildUserMapper.map(recruitPost.author))
        }
    }
}