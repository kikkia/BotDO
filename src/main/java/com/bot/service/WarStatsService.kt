package com.bot.service

import com.bot.db.entities.WarEntity
import com.bot.db.entities.WarStatsEntity
import com.bot.db.repositories.WarStatsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
open class WarStatsService(private val warStatsRepository: WarStatsRepository) {
    open fun getAllByWarId(war: Int) : List<WarStatsEntity> {
        return warStatsRepository.findAllByWarId(war)
    }

    open fun addWarStats(war: WarEntity, img: String) : WarStatsEntity {
        return warStatsRepository.save(WarStatsEntity(0, war, img))
    }
}