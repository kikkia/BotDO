package com.bot.service

import com.bot.db.entities.*
import com.bot.db.repositories.WarDmSignupRepository
import com.bot.db.repositories.WarRepository
import com.bot.utils.FormattingUtils
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors
import javax.transaction.Transactional

@Transactional
@Service
open class WarService(private val warRepository: WarRepository,
                      private val warDmSignupRepository: WarDmSignupRepository,
                      private val warVodService: WarVodService,
                      private val warStatsService: WarStatsService,
                      private val textChannelService: TextChannelService) {

    open fun getWarByMessageId(messageId: String) : Optional<WarEntity> {
        return warRepository.findByMessageId(messageId)
    }

    /**
     * Get a war entity from a dm message id if that message is for a war signup
     */
    open fun getWarByDmMessageId(messageId: String) : Optional<WarEntity> {
        val signupOpt = warDmSignupRepository.findByMessageIdAndActiveTrue(messageId)
        if (signupOpt.isPresent) {
            return Optional.of(signupOpt.get().warEntity)
        }
        return Optional.empty()
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

    // Gets a war that has started in the last hour or is starting in the next hour
    open fun getByGuildInNextHour(guild: GuildEntity) : Optional<WarEntity> {
        return warRepository.findByGuildIdAndWarTimeAfterAndTimeBefore(guild.bdoGuild!!.id,
                Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)),
                Timestamp.from(Instant.now().plus(1, ChronoUnit.HOURS)));
    }

    open fun setArchived(war: WarEntity) : WarEntity {
        deactivateDmSignup(war)
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

    open fun createWar(time: Instant, messageId: String, channelEntity: TextChannelEntity, guild: BDOGuildEntity) : WarEntity {
        val newWar = WarEntity(0, Timestamp.from(time), listOf(), messageId, channelEntity, guild)
        return save(newWar)
    }

    open fun addDmSignupMessage(war: WarEntity, messageId: String, userId: String) : WarDmSignupEntity {
        val signup = WarDmSignupEntity(0, messageId, userId, war)
        return warDmSignupRepository.save(signup)
    }

    open fun deactivateDmSignup(war: WarEntity, userId: String) {
        val signupEntities = warDmSignupRepository.findAllByWarEntityAndUserId(war, userId)
        for (signupEntity in signupEntities) {
            deactivateDmSignup(signupEntity)
        }
    }

    open fun deactivateDmSignup(war: WarEntity) {
        val signupEntities = warDmSignupRepository.findAllByWarEntityAndActiveTrue(war)
        for (signupEntity in signupEntities) {
            deactivateDmSignup(signupEntity)
        }
    }

    open fun deactivateDmSignup(entity: WarDmSignupEntity) {
        entity.active = false
        warDmSignupRepository.save(entity)
    }

    open fun getActiveDmSignups() : List<WarDmSignupEntity> {
        return warDmSignupRepository.findAllByActiveTrue()
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

    open fun getAllUpcoming() : List<WarEntity> {
        return warRepository.findAllByWarTimeAfter(Timestamp.from(Instant.now()))
    }

    open fun refreshMessage(guild: Guild, war: WarEntity) {
        val channel = guild.getTextChannelById(war.channel.id) ?: return

        val message = if (war.archived)
            FormattingUtils.generateArchivedWarMessage(war,
                    warVodService.getAllByWarId(war.id),
                    warStatsService.getAllByWarId(war.id))
        else
            FormattingUtils.generateWarMessage(war)
        channel.editMessageEmbedsById(war.messageId, message).queue()
    }
}