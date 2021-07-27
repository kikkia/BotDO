package com.bot.batch.readers

import com.bot.db.entities.WarEntity
import com.bot.service.WarService
import org.springframework.batch.item.ItemReader

class WarReader(private val warService: WarService) : ItemReader<List<WarEntity>> {
    override fun read(): List<WarEntity> {
        return warService.getAllUpcoming()
    }
}