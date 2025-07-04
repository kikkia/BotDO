package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.TextChannelEntity
import com.bot.models.WarDay
import com.bot.service.GuildService
import com.bot.service.TextChannelService
import com.bot.service.WarService
import com.bot.utils.CommandParsingUtils
import com.bot.utils.FormattingUtils
import com.bot.utils.WarUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit


@Component
class AddWarCommand(private val warService: WarService, guildService: GuildService,
                    private val textChannelService: TextChannelService,
                    @Value("\${war.startTime}") private val warStartTime: String) : WarCommand(guildService) {

    init {
        this.name = "addwar"
        this.help = "Add a one off war on the given date"
        this.arguments = "Date to make war (dd-mm-yyyy format) and an optional channel to post it in.)"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        if (command.args.isBlank()) {
            command.replyWarning("You need to include some arguments with this command: `$arguments`")
            return
        }

        val date = CommandParsingUtils.parseArgsToDate(command.args, warStartTime)
        if (date == null) {
            command.replyWarning("Could not parse time, please make sure that you are formatting it correctly (dd-mm-yyyy)")
            return
        } else if (date.toInstant().isBefore(Instant.now())) {
            command.replyWarning("That date has already happened :cry:")
            return
        } else if (date.toInstant().isAfter(Instant.now().plus(14, ChronoUnit.DAYS))) {
            command.replyWarning("You cannot add a war more than 2 weeks in advance :cry:")
            return
        } else if (warService.getWarByGuildAndDate(guild, date).isPresent) {
            command.replyWarning("You already have war setup for that day.")
            return
        } else if (guild.bdoGuild!!.getWarDays().contains(WarDay.getFromDate(date))) {
            command.replyWarning("You already have regular wars setup for that day of the week.")
            return
        }

        val channel: TextChannel?
        val channelEntity: TextChannelEntity
        if (command.message.mentions.channels.isEmpty()) {
            // Create new channel
            channel = command.guild.createTextChannel("War ${FormattingUtils.formatDateToBasicString(date)}").complete() as TextChannel
            channelEntity = textChannelService.add(channel, guild)
        } else {
            channel = command.message.mentions.channels[0] as TextChannel
            channelEntity = textChannelService.getById(channel.id)
        }

        WarUtils.sendNewWar(guild, date, channel, channelEntity, warService)
        command.reactSuccess()
    }
}