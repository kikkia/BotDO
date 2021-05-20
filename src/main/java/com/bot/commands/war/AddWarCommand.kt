package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.GuildService
import com.bot.service.TextChannelService
import com.bot.service.WarService
import com.bot.utils.FormattingUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


@Component
class AddWarCommand(val warService: WarService, guildService: GuildService, val textChannelService: TextChannelService) : WarCommand(guildService) {

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

        val date = parseArgsToDate(command.args)
        if (date == null) {
            command.replyWarning("Could not parse time, please make sure that you are formatting it correctly (dd-mm-yyyy)")
            return
        } else if (date.toInstant().isBefore(Instant.now())) {
            command.replyWarning("That date has already happened :unimpressed:")
            return
        }
        
        var channel: TextChannel? = null
        if (command.message.mentionedChannels.isEmpty()) {
            // Create new channel
            channel = command.guild.createTextChannel("War ${FormattingUtils.formatDateToChannelName(date)}").complete()
            textChannelService.add(channel, guild)
        } else {
            channel = command.message.mentionedChannels[0]
        }

        val warMessage = channel.sendMessage("hey").complete()
        val channelEntity = textChannelService.getById(channel.id)

        warService.createWar(date.toInstant(), warMessage.id, channelEntity, guild.bdoGuild!!);
        command.replySuccess(date.toString())
    }

    fun parseArgsToDate(input: String) : Date? {
        val args = input.split(" ")
        val formatter: SimpleDateFormat = SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("America/Chicago")
        var date: Date? = null
        for (arg in args) {
            val timeToParse = "$arg 20:00:00"
            try {
                date = formatter.parse(timeToParse)
                break
            } catch (e: ParseException) {
                continue
            }
        }
        return date
    }
}