package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.GuildService
import com.bot.service.WarService
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.lang.NumberFormatException

@Component
class NoShowCommand(val warService: WarService, guildService: GuildService) : WarCommand(guildService) {

    init {
        this.name = "noshow"
        this.help = "Marks people who signed up as no shows."
        this.guildOnly = true
        this.arguments = "ID of war::comma-separated list of members (as their name shows on attendance sheet)"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        try {
            val id = Integer.parseInt(command.args.split("::")[0])

            val warOpt = warService.getByGuildAndId(guild, id)
            if (warOpt.isEmpty) {
                command.replyWarning("War not found, make sure you are using the right ID")
                return
            }
            val war = warOpt.get()
            if (!war.archived) {
                command.replyWarning("You need to archive a war (using the ,archive command)" +
                        " before you can edit attendance.")
                return
            }

            val namesToChange = command.args.split("::")[1].split(",")
            val namesChanged = mutableListOf<String>()
            for (attendee in war.attendees) {
                if (namesToChange.contains(attendee.user.getEffectiveName()) && !attendee.notAttending) {
                    warService.noShow(war, attendee)
                    namesChanged.add(attendee.user.getEffectiveName())
                }
            }

            warService.refreshMessage(command.guild, war)

            if (namesChanged.size == namesToChange.size) {
                command.reactSuccess()
            } else {
                command.replyWarning("Recorded no show for (${namesChanged.toString()}) Please make sure all names are " +
                        "spelled the same as on the attendance list.")
            }

        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid input, please put in a valid id (integer)")
            return
        }
    }
}