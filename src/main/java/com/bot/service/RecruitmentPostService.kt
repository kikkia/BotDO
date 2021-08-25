package com.bot.service

import com.bot.db.entities.UserEntity.Companion.from
import com.bot.db.entities.GuildEntity.Companion.partialFrom
import com.bot.db.repositories.RecruitmentPostRepository
import com.bot.db.entities.RecruitmentPostEntity
import com.bot.db.entities.GuildEntity
import com.bot.db.mapper.RecruitmentPostMapper
import com.bot.api.models.RecruitmentPost
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.stream.Collectors

@Service
open class RecruitmentPostService(private val recruitmentPostRepository: RecruitmentPostRepository) {

    open fun add(author: User, guild: Guild, channel: String): RecruitmentPostEntity {
        return recruitmentPostRepository.save(RecruitmentPostEntity(0,
                from(author),
                partialFrom(guild),
                channel,
                Timestamp.from(Instant.now())))
    }

    open fun getAllForGuildInPast(guild: GuildEntity, afterTimestamp: Timestamp) : List<RecruitmentPost> {
        return recruitmentPostRepository.findAllByGuildAndTimestampAfter(guild, afterTimestamp)
                .stream()
                .map { RecruitmentPostMapper.map(it) }
                .collect(Collectors.toList())
    }
}