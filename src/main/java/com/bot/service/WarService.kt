package com.bot.service

import com.bot.db.entities.*
import com.bot.db.repositories.WarRepository
import com.bot.utils.FormattingUtils
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import javax.transaction.Transactional

@Transactional
@Service
open class WarService(private val warRepository: WarRepository,
                      private val warVodService: WarVodService,
                      private val warStatsService: WarStatsService,
                      private val textChannelService: TextChannelService) {

    open fun getWarByMessageId(messageId: String) : Optional<WarEntity> {
        return warRepository.findByMessageId(messageId)
    }

    open fun getWarByGuildAndDate(guild: GuildEntity, date: Date) : Optional<WarEntity> {
        return warRepository.findByGuildIdAndWarTime(guild.bdoGuild!!.id, Timestamp.from(date.toInstant()))
    }

    open fun getAllByChannelDoneAndActive(channelId: String) : List<WarEntity> {
        return warRepository.findAllByChannelIdAndArchivedAndWarTimeBefore(channelId, false, Timestamp.from(Instant.now()))
    }

    open fun getByGuildAndId(guild: GuildEntity, id: Int) : Optional<WarEntity> {
        return warRepository.findByGuildIdAndId(guild.bdoGuild!!.id, id)
    }

    open fun setArchived(war: WarEntity) : WarEntity {
        war.archived = true
        return save(war)
    }

    open fun setMessageId(war: WarEntity, messageId: String) : WarEntity {
        war.messageId = messageId
        return save(war)
    }

    open fun setChannel(war: WarEntity, channelId: String) : WarEntity {
        war.channel = textChannelService.getById(channelId)
        return save(war)
    }

    open fun wonWar(war: WarEntity) : WarEntity {
        war.won = true
        return save(war)
    }

    open fun createWar(time: Instant, messageId: String, channel: TextChannel, guild: BDOGuildEntity) : WarEntity {
        val newWar = WarEntity(0, Timestamp.from(time), listOf(), messageId, channel, guild)
        return save(newWar)
    }

    open fun addAttendee(war: WarEntity, user: UserEntity, maybe: Boolean = false) : WarEntity {
        val attendees = war.attendees.toMutableList()
        val entity = WarAttendanceEntity(0, war, user)
        entity.maybe = maybe
        attendees.add(entity)
        war.attendees = attendees
        return save(war)
    }

    open fun notAttending(war: WarEntity, user: UserEntity) : WarEntity {
        val attendees = war.attendees.toMutableList()
        val entity = WarAttendanceEntity(0, war, user)
        entity.notAttending = true
        attendees.add(entity)
        war.attendees = attendees
        return save(war)
    }

    open fun noShow(war: WarEntity, attendee: WarAttendanceEntity) : WarEntity {
        val attendees = war.attendees.toMutableList()
        attendees.remove(attendee)
        attendee.noShow = true
        attendees.add(attendee)
        war.attendees = attendees
        return save(war)
    }

    open fun removeAttendee(war: WarEntity, user: UserEntity) : WarEntity {
        val attendees = war.attendees.stream().filter{ it.user.id != user.id}.collect(Collectors.toList())
        war.attendees = attendees
        return save(war)
    }

    open fun refreshAttendee(war: WarEntity, user: UserEntity) : WarEntity {
        removeAttendee(war, user)
        return addAttendee(war, user)
    }

    open fun save(war: WarEntity) : WarEntity {
        return warRepository.save(war)
    }

    open fun refreshMessage(guild: Guild, war: WarEntity) {
        val channel = guild.getTextChannelById(war.channel.id) ?: return

        val message = if (war.archived)
            FormattingUtils.generateArchivedWarMessage(war,
                    warVodService.getAllByWarId(war.id),
                    warStatsService.getAllByWarId(war.id))
        else
            FormattingUtils.generateWarMessage(war)
        channel.editMessageById(war.messageId, message).queue()
    }
}