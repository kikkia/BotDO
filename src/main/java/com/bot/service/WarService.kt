package com.bot.service

import com.bot.db.entities.BDOGuildEntity
import com.bot.db.entities.UserEntity
import com.bot.db.entities.WarAttendanceEntity
import com.bot.db.entities.WarEntity
import com.bot.db.repositories.WarRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

@Service
class WarService(private val warRepository: WarRepository) {

    fun getWarByMessageId(messageId: String) : Optional<WarEntity> {
        return warRepository.findByMessageId(messageId)
    }

    fun createWar(time: Instant, messageId: String, guild: BDOGuildEntity) : WarEntity {
        val newWar = WarEntity(0, Timestamp.from(time), listOf(), messageId, guild)
        return save(newWar)
    }

    fun addAttendee(war: WarEntity, user: UserEntity) : WarEntity {
        val attendees = war.attendees.toMutableList()
        attendees.add(WarAttendanceEntity(0, war, user))
        return save(war)
    }

    fun removeAttendee(war: WarEntity, user: UserEntity) : WarEntity {
        val attendees = war.attendees.stream().filter{ it.user.id != user.id}.collect(Collectors.toList())
        war.attendees = attendees
        return save(war)
    }

    fun save(war: WarEntity) : WarEntity {
        return warRepository.save(war)
    }
}