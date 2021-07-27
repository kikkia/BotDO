package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.models.WarDay
import com.bot.service.BdoGuildService
import com.bot.service.GuildService
import com.bot.service.TextChannelService
import com.bot.service.WarService
import com.bot.utils.DateUtils
import com.bot.utils.WarUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.TextChannel
import org.springframework.stereotype.Component
import java.time.format.TextStyle
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Component
class SetWarDaysCommand(val warService: WarService, val bdoGuildService: BdoGuildService,
                        guildService: GuildService, val textChannelService: TextChannelService) : WarCommand(guildService) {
    init {
        this.name = "setwardays"
        this.help = "Sets up channels for regular wars on given days."
        this.arguments = "Day of weeks, comma separated, in english. (e.g. Sunday, Monday, Saturday)"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        if (command.args.isBlank()) {
            bdoGuildService.setWarDays(guild.bdoGuild, mutableListOf())
            command.replySuccess("Cleared all war days")
            return
        }
        val days = HashSet<WarDay>()
        for (arg in command.args.split(",")) {
            val day = WarDay.getFromString(arg.trim())
            if (day == null) {
                command.replyWarning("Failed to find day: `$arg` please make sure days are spelled correctly.")
                return
            }
            days.add(day)
        }
        var existingDays = WarDay.getAllDays(guild.bdoGuild!!.warDays)
        val addedDays = HashMap<WarDay, TextChannel>()
        // Do nothing on days no longer warred
        // Do nothing on existing days
        // Create channels and first war on new days
        for (day in days) {
            val channelName = "war-${day.day.getDisplayName(TextStyle.FULL, Locale.US)}"
            if (!existingDays.contains(day)) {
                // Checks for existing wars on the upcoming day of week
                val warDate = DateUtils.getNextWarDate(day)
                val existingWarOptional = warService.getWarByGuildAndDate(guild, warDate)
                if (existingWarOptional.isPresent) {
                    // Handle the case of a war existing but not being on an official war day
                    val existingWar = existingWarOptional.get()
                    val existingChannel = command.guild.getTextChannelById(existingWar.channel.id)
                            ?: // Channel deleted? Do nothing
                            continue
                    existingChannel.manager.setName(channelName).queue()
                    addedDays[day] = existingChannel
                    continue
                }
                
                // Setup new channel
                addedDays[day] = command.guild.createTextChannel(
                        channelName).complete()
                val channel = textChannelService.add(addedDays[day], guild)
                WarUtils.sendNewWar(guild, warDate, addedDays[day]!!, channel, warService)
            }
            // Remove from existing list
            existingDays = existingDays.stream().filter{ it.id != day.id }.collect(Collectors.toList())
        }

        bdoGuildService.setWarDays(guild.bdoGuild, days.toList())
        val removed = existingDays.stream().map {it.name}.collect(Collectors.toList())
        val added = addedDays.entries.stream().map { "${it.key.name} - ${it.value.asMention}"}.collect(Collectors.toList())
        command.replySuccess("Successfully set war days:\nAdded: ${added.toString()}\nRemoved: ${removed.toString()}")
    }
}