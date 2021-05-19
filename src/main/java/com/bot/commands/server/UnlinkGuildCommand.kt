package com.bot.commands.server

import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class UnlinkGuildCommand(val guildService: GuildService) : Command() {
    init {
        this.name = "unlinkguild"
        this.help = "Unlinks the currently linked in game guild from this server."
        this.userPermissions = listOf(Permission.ADMINISTRATOR).toTypedArray()
    }

    override fun execute(commandEvent: CommandEvent?) {
        val guild = guildService.getById(commandEvent!!.guild.id)
        guildService.setBdoGuild(guild, null)
        commandEvent.replySuccess("Unlinked this discord server from in game guild. Wars and member tracking will " +
                "now be disabled on this server.")
    }
}