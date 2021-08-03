package com.bot.batch.readers

import com.bot.db.entities.WarEntity
import com.bot.service.WarService
import org.springframework.batch.item.ItemReader

class WarReader(private val warService: WarService) : ItemReader<List<WarEntity>> {
    // The batch job goes until the reader returns null, we want to get all upcoming
    // and return null the next time to not repeat the step indefinitely
    var ran = false

    override fun read(): List<WarEntity>? {
        if (!ran) {
            ran = true
            return warService.getAllUpcoming()
        } else {
            ran = false
            return null
        }
    }
}