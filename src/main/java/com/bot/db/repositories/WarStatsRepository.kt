package com.bot.db.repositories

import com.bot.db.entities.WarStatsEntity
import org.springframework.data.repository.CrudRepository

interface WarStatsRepository : CrudRepository<WarStatsEntity, Int> {
    fun findAllByWarId(war: Int): List<WarStatsEntity>
}