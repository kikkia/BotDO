package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.GuildService
import com.bot.service.UserService
import com.bot.service.WarService
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.lang.NumberFormatException

@Component
class AttendedCommand(val warService: WarService, guildService: GuildService, val userService: UserService) : WarCommand(guildService) {

    init {
        this.name = "attended"
        this.help = "Marks people who signed up as maybes or did not sign up as attended."
        this.guildOnly = true
        this.arguments = "ID of war::comma-separated list of members (as their name shows on attendance sheet or @mentions)"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        try {
            val id = Integer.parseInt(command.args.split("::")[0])

            val warOpt = warService.getByGuildAndId(guild, id)
            if (warOpt.isEmpty) {
                command.replyWarning("War not found, make sure you are using the right ID")
                return
            }
            var war = warOpt.get()
            if (!war.archived) {
                command.replyWarning("You need to archive a war (using the ,archive command)" +
                        " before you can edit attendance.")
                return
            }

            val namesToChange = command.args.split("::")[1].split(",")
            val namesChanged = mutableListOf<String>()
            // Names in args, check for existing only
            for (attendee in war.attendees) {
                if (namesToChange.contains(attendee.user.getEffectiveName())) {
                    warService.refreshAttendee(war, attendee.user)
                    namesChanged.add(attendee.user.getEffectiveName())
                }
            }

            // Mentions in args (Check existing and non existing)
            for (mention in command.message.mentions.members) {
                val attendOpt = war.attendees.stream().filter { it.user.id == mention.user.id}.findAny()
                if (attendOpt.isEmpty) {
                    val user = userService.getById(mention.user.id)
                    warService.addAttendee(war, userService.getById(mention.user.id))
                    namesChanged.add(user.getEffectiveName())
                } else {
                    val attendee = attendOpt.get()
                    warService.refreshAttendee(war, attendee.user)
                    namesChanged.add(attendee.user.getEffectiveName())
                }
            }

            warService.refreshMessage(command.guild, war)

            if (namesChanged.size == namesToChange.size) {
                command.reactSuccess()
            } else {
                command.replyWarning("Recorded attendance for (${namesChanged.toString()}) Please make sure all names are " +
                        "spelled the same as on the attendance list. NOTE: You can only add people who did not signup to this war using mentions.")
            }

        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid input, please put in a valid id (integer)")
            return
        }
    }
}