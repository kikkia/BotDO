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
class AddVodCommand(val warService: WarService,
                    val warVodService: WarVodService,
                    guildService: GuildService) : WarCommand(guildService) {

    init {
        this.name = "addvod"
        this.guildOnly = true
        this.arguments = "id::name::url"
        this.help = "adds a vod to an archived war"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        val war: WarEntity
        val args = command.args.split("::")
        if (args.size != 3) {
            command.replyWarning("Wrong number of arguments. Format is warId::name::url")
            return
        }
        if (args[2].length > 500) {
            command.replyWarning("That url is very long, please use a shorter one")
            return
        }
        if (args[1].length > 30) {
            command.replyWarning("Name can be no longer than 30")
            return
        }

        try {
            val id = Integer.parseInt(args[0])
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
        warVodService.addWarVod(war, args[2], args[1])
        warService.refreshMessage(command.guild, war)
        command.reactSuccess()
    }
}