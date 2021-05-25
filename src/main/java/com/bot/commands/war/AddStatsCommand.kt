package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.db.entities.WarEntity
import com.bot.service.GuildService
import com.bot.service.WarService
import com.bot.service.WarStatsService
import com.bot.service.WarVodService
import com.bot.utils.WarUtils
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.lang.NumberFormatException

@Component
class AddStatsCommand(val warService: WarService,
                      val warStatsService: WarStatsService,
                      guildService: GuildService,
                      val warVodService: WarVodService) : WarCommand(guildService) {
    init {
        this.name = "addstats"
        this.help = "Adds stats image to war results"
        this.arguments = "warId (and attached image)"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        val war: WarEntity
        if (command.message.attachments.isEmpty()) {
            command.replyWarning("You need to attach a screenshot of stats")
            return
        }
        try {
            val id = Integer.parseInt(command.args)
            val warOpt = warService.getByGuildAndId(guild, id)
            if (warOpt.isEmpty) {
                command.replyWarning("War not found, make sure you are using the right Id")
                return
            }
            war = warOpt.get()
        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid input, please put in a valid id (integer)")
            return
        }
        if (!war.archived) {
            command.replyWarning("You need to archive a war (using the ,archive command) before you can set victory.")
            return
        }
        warStatsService.addWarStats(war, command.message.attachments[0].url)
        warService.refreshMessage(command.guild, war)
        command.reactSuccess()
    }
}