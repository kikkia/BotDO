package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.models.WarDay
import com.bot.models.WarNode
import com.bot.service.*
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.lang.Integer.parseInt

@Component
class NodeCommand(val warService: WarService,
                  guildService: GuildService) : WarCommand(guildService) {
    init {
        this.name = "node"
        this.help = "Sets the node for a war on a given date"
        this.arguments = "<warId::Name of the node>"
    }

    override fun executeCommand(command: CommandEvent, guild: GuildEntity) {
        if (command.args.isBlank()) {
            command.replyWarning("You need to include some arguments with this command: `$arguments`")
            return
        }
        val argsSplit = command.args.split("::")
        val id: Int?
        try {
            id = parseInt(argsSplit[0])
        } catch (e: NumberFormatException) {
            command.replyWarning("Invalid id, id is an integer, on the footer of the signup/archived message.")
            return
        }
        if (argsSplit.size != 2) {
            command.replyWarning("Format or arguments is incorrect, " +
                    "please make sure your arguments are correct. (e.g. dd-mm-yyyy / Node name)")
            return
        }

        val warOpt = warService.getByGuildAndId(guild, id)
        if (warOpt.isEmpty) {
            command.replyWarning("No existing war found on that date, if its one of your guild war days you will need " +
                    "to wait till the message for that date is made in the channel. Otherwise you can add a one time " +
                    "war with the `,addwar` command.")
            return
        }
        val war = warOpt.get()

        val warNode = WarNode.getNodeFromName(argsSplit[1].trim(), WarDay.getFromDate(war.warTime))
        if (warNode == null) {
            command.replyWarning("Node not found, please make sure it's spelled correctly.")
            return
        }

        war.setNode(warNode)
        warService.save(war)
        val channel = command.guild.getTextChannelById(war.channel.id)
        if (channel == null) {
            command.replyWarning("I was unable to find the channel that war is in. Did you delete it?")
            return
        }
        warService.refreshMessage(command.guild, war)
        command.reactSuccess()
    }


}