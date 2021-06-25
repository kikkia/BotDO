package com.bot.service

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.GuildEntity.Companion.partialFrom
import com.bot.db.entities.RecruitBellEntity
import com.bot.db.entities.UserEntity.Companion.from
import com.bot.db.repositories.RecruitBellRepository
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
open class RecruitBellService(private val recruitBellRepository: RecruitBellRepository) {
    private val BELL_REFRESH_COOLDOWN = 3 * 60 * 60

    open fun add(author: User, guild: Guild): RecruitBellEntity {
        return recruitBellRepository.save(RecruitBellEntity(0,
                from(author),
                partialFrom(guild)))
    }

    open fun isOnCooldown(guild: GuildEntity) : Boolean {
        return recruitBellRepository.existsByGuildAndCreatedAfter(guild,
                Timestamp.from(
                        Instant.now().minusSeconds(
                                BELL_REFRESH_COOLDOWN.toLong())))
    }

    open fun getLastByGuild(guild: GuildEntity) : Optional<RecruitBellEntity> {
        return recruitBellRepository.getByGuildAndCreatedAfter(guild,
                Timestamp.from(
                        Instant.now().minusSeconds(
                                BELL_REFRESH_COOLDOWN.toLong())))
    }
}