package com.bot.service

import com.bot.db.entities.WarEntity
import com.bot.db.entities.WarVodEntity
import com.bot.db.repositories.WarVodRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
open class WarVodService(private val warVodRepository: WarVodRepository) {
    open fun getAllByWarId(war: Int) : List<WarVodEntity> {
        return warVodRepository.findAllByWarId(war)
    }

    open fun addWarVod(war: WarEntity, link: String, name: String) : WarVodEntity {
        return warVodRepository.save(WarVodEntity(0, war, link, name))
    }
}