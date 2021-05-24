package com.bot.service

import com.bot.db.entities.*
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

    fun getWarByGuildAndDate(guild: GuildEntity, date: Date) : Optional<WarEntity> {
        return warRepository.findByGuildIdAndWarTime(guild.bdoGuild!!.id, Timestamp.from(date.toInstant()))
    }

    fun createWar(time: Instant, messageId: String, channel: TextChannel, guild: BDOGuildEntity) : WarEntity {
        val newWar = WarEntity(0, Timestamp.from(time), listOf(), messageId, channel, guild)
        return save(newWar)
    }

    fun addAttendee(war: WarEntity, user: UserEntity, maybe: Boolean = false) : WarEntity {
        val attendees = war.attendees.toMutableList()
        val entity = WarAttendanceEntity(0, war, user)
        entity.maybe = maybe
        attendees.add(entity)
        war.attendees = attendees
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