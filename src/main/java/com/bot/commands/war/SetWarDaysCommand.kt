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
        var existing = WarDay.getAllDays(guild.bdoGuild!!.warDays)
        val addedDays = HashMap<WarDay, TextChannel>()
        // Do nothing on days no longer warred
        // Do nothing on existing days
        // Create channels and first war on new days
        for (day in days) {
            if (!existing.contains(day)) {
                // Setup new channel
                addedDays[day] = command.guild.createTextChannel(
                        "war-${day.day.getDisplayName(TextStyle.FULL, Locale.US)}").complete()
                textChannelService.add(addedDays[day], guild)
                WarUtils.sendNewWar(guild, DateUtils.getNextWarDate(day), addedDays[day]!!, textChannelService, warService)
            }
            // Remove from existing list
            existing = existing.stream().filter{ it.id != day.id }.collect(Collectors.toList())
        }

        bdoGuildService.setWarDays(guild.bdoGuild, days.toList())
        val removed = existing.stream().map {it.name}.collect(Collectors.toList())
        val added = addedDays.entries.stream().map { "${it.key.name} - ${it.value.asMention}"}.collect(Collectors.toList())
        command.replySuccess("Successfully set war days:\nAdded: ${added.toString()}\nRemoved: ${removed.toString()}")
    }
}