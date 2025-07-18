package com.bot.commands.server

import com.bot.commands.RequiredArgsCommand
import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class WarArchiveCommand(val guildService: GuildService) : RequiredArgsCommand() {
    init {
        this.name = "wararchive"
        this.aliases = listOf("setarchive", "setarchivechannel").toTypedArray()
        this.help = "Set a channel where war messages and info will go once they are finished"
        this.arguments = "Channel mention (#channel-name)"
        this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
    }

    override fun executeCommand(commandEvent: CommandEvent?) {
        if (commandEvent!!.message.mentions.channels.isEmpty()) {
            commandEvent.replyWarning("You need to link a channel when using this command. (#channel-name)")
            return
        }
        val guild = guildService.getById(commandEvent.guild.id)
        guildService.setArchiveChannel(guild, commandEvent.message.mentions.channels[0].id)
        commandEvent.reactSuccess()
    }
}