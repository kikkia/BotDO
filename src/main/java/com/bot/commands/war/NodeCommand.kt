package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.models.WarDay
import com.bot.models.WarNode
import com.bot.service.GuildService
import com.bot.service.TextChannelService
import com.bot.service.WarService
import com.bot.utils.CommandParsingUtils
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class NodeCommand(val warService: WarService, guildService: GuildService, val textChannelService: TextChannelService) : WarCommand(guildService) {
    init {
        this.name = "node"
        this.help = "Sets the node for a war on a given date"
        this.arguments = "<date of the war / Name of the node>"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        if (command.args.isBlank()) {
            command.replyWarning("You need to include some arguments with this command: `$arguments`")
            return
        }
        val argsSplit = command.args.split("/")
        val date = CommandParsingUtils.parseArgsToDate(argsSplit[0])
        if (date == null) {
            command.replyWarning("Date not valid, please make sure your arguments are correct. (e.g. dd-mm-yyyy / Node name)")
            return
        }
        val warNode = WarNode.getNodeFromName(argsSplit[1], WarDay.getFromDate(date))
        if (warNode == null) {
            command.replyWarning("Node not found, please make sure it's spelled correctly.")
            return
        }
        val warOpt = warService.getWarByGuildAndDate(guild, date)
        if (warOpt.isEmpty) {
            command.replyWarning("No existing war found on that date, if its one of your guild war days you will need " +
                    "to wait till the message for that date is made in the channel. Otherwise you can add a one time " +
                    "war with the `,addwar` command.")
            return
        }
        val war = warOpt.get()
        war.setNode(warNode)
        warService.save(war)
        command.reactSuccess()
    }


}