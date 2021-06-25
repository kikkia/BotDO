package com.bot.db.repositories

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.RecruitBellEntity
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.*

interface RecruitBellRepository: CrudRepository<RecruitBellEntity, Int> {
    fun findAllByGuildAndCreatedAfter(guild: GuildEntity, created: Timestamp) : List<RecruitBellEntity>
    fun existsByGuildAndCreatedAfter(guild: GuildEntity, created: Timestamp) : Boolean
    fun getByGuildAndCreatedAfter(guild: GuildEntity, created: Timestamp) : Optional<RecruitBellEntity>
}