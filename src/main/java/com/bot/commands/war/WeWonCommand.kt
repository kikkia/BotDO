package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.GuildService
import com.bot.service.WarService
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.lang.NumberFormatException

@Component
class WeWonCommand(val warService: WarService,
                   guildService: GuildService) : WarCommand(guildService) {
    init {
        this.name = "wewon"
        this.help = "Marks a given war as won"
        this.guildOnly = true
        this.arguments = "ID of war you won (Found on footer of message)"
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
            if (!war.archived) {
                command.replyWarning("You need to archive a war (using the ,archive command) before you can set victory.")
                return
            }
            warService.wonWar(war)

            warService.refreshMessage(command.guild, war)
            command.reactSuccess()
        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid input, please put in a valid id (integer)")
            return
        }
    }
}