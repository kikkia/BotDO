package com.bot.db.repositories

import com.bot.db.entities.WarVodEntity
import org.springframework.data.repository.CrudRepository

interface WarVodRepository : CrudRepository<WarVodEntity, Int> {
    fun findAllByWarId(war: Int): List<WarVodEntity>
}