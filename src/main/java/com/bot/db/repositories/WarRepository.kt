package com.bot.db.repositories

import com.bot.db.entities.WarEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.util.*

interface WarRepository : CrudRepository<WarEntity, Int> {
    fun findAllByGuildId(guildId: Int): List<WarEntity>

    fun findByMessageId(messageId: String) : Optional<WarEntity>

    fun findByGuildIdAndWarTime(guildId: Int, warTime: Timestamp) : Optional<WarEntity>

    fun findAllByChannelIdAndArchivedAndWarTimeBefore(channelID: String, archived: Boolean, @Param("warTime") time: Timestamp) : List<WarEntity>

    fun findByGuildIdAndId(guildId: Int, id: Int) : Optional<WarEntity>
    // Get past wars by guild id
    fun findAllByGuildIdAndWarTimeBefore(guildId: Int, @Param("warTime") time: Timestamp): List<WarEntity>

    // Get future wars by guild id
    fun findAllByGuildIdAndWarTimeAfter(guildId: Int, @Param("warTime") time: Timestamp): List<WarEntity>
}