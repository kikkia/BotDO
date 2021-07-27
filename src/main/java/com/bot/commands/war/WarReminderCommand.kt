package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.WarEntity
import com.bot.service.GuildService
import com.bot.service.WarService
import com.bot.utils.Constants
import com.bot.utils.FormattingUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.entities.User
import org.springframework.stereotype.Component
import java.lang.NumberFormatException

@Component
class WarReminderCommand(val warService: WarService,
                         guildService: GuildService) : WarCommand(guildService) {
    init {
        this.name = "remindwar"
        this.help = "DMs a reminder to everyone who has not signed up for the given war."
        this.arguments = "<id of war to remind for>"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        try {
            val id = Integer.parseInt(command.args)
            val warOpt = warService.getByGuildAndId(guild, id)
            if (warOpt.isEmpty) {
                command.replyWarning("War not found, make sure you are using the right ID")
                return
            }
            val war = warOpt.get()
            if (war.past()) {
                command.replyWarning("You cannot remind people to sign up for a war that has happened.")
                return
            }

            val channel = command.guild.getTextChannelById(war.channel.id)
            if (channel == null) {
                command.replyError("Channel for war not found. Something is really wrong here...")
                return
            }
            channel.sendTyping().queue()
            val failedUsers = mutableListOf<String>()
            val sentUsers = mutableListOf<String>()
            val signedUpUserIds = war.attendees.asSequence().map { it.user.id }.toList()

            for (member in channel.members) {
                if (!member.user.isBot) {
                    if (!signedUpUserIds.contains(member.user.id)) {
                        try {
                            sentUsers.add(sendDmMessage(war, member.user))
                        } catch (e: Exception) {
                            failedUsers.add(member.effectiveName)
                            print("Failed to remind user")
                            e.printStackTrace()
                        }
                    }
                }
            }

            command.replySuccess("Successfully sent reminder to ${sentUsers.size} users.")
            if (failedUsers.size > 0) {
                command.replyWarning("Failed to send reminders to ${failedUsers.toString()}")
            }
        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid input, please put in a valid id (integer)")
            return
        }
    }

    // TODO: Get this out of here, cannot use discord service as it makes a circular dep
    fun sendDmMessage(war: WarEntity, user: User) : String {
        val privateChannel = user.openPrivateChannel().complete()
        val message = privateChannel.sendMessage(FormattingUtils.generateDmWarReminder(war)).complete()
        for (reaction in Constants.WAR_REACTIONS) {
            message.addReaction(reaction).queue()
        }
        warService.addDmSignupMessage(war, message.id, user.id)
        return user.id
    }
}