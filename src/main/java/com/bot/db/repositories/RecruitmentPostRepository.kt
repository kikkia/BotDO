package com.bot.db.repositories

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.RecruitmentPostEntity
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp

interface RecruitmentPostRepository : CrudRepository<RecruitmentPostEntity, Int> {
    fun findAllByGuildAndTimestampAfter(guild: GuildEntity, created: Timestamp) : List<RecruitmentPostEntity>
}
