package com.bot.db.repositories

import com.bot.db.entities.BDOGuildEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface BdoGuildRepository : CrudRepository<BDOGuildEntity?, Int?> {
    fun findByNameAndRegion(name: String?, region: String?): Optional<BDOGuildEntity?>?
}