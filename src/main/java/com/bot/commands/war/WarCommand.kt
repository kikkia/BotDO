package com.bot.commands.war

import com.bot.db.entities.GuildEntity
import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import java.lang.Exception

abstract class WarCommand(val guildService: GuildService) : Command() {

    init {
        this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
        this.botPermissions = listOf(Permission.MANAGE_SERVER, Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE).toTypedArray()
        this.guildOnly = true
    }

    override fun execute(command: CommandEvent?) {
        // Enforce guild link to do war commands
        val guild = guildService.getById(command!!.guild.id)
        if (guild.bdoGuild == null) {
            command.replyWarning("You need to like the server to an in game guild first using the `linkguild` command.")
            return
        }
        try {
            executeCommand(command, guild)
        } catch (e: Exception) {
            command.replyError("Something went wrong executing the command. Please try again and if this issue " +
                    "continues please reach out for support.")
            e.printStackTrace()
        }
    }

    abstract fun executeCommand(command: CommandEvent, guild: GuildEntity)
}