package com.bot.commands.server

import com.bot.commands.RequiredArgsCommand
import com.bot.models.Region
import com.bot.service.BdoGuildService
import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.Permission
import org.springframework.stereotype.Component

@Component
class LinkGuildCommand(val guildService: GuildService, val bdoGuildService: BdoGuildService) : RequiredArgsCommand() {

    init {
        this.name = "linkguild"
        this.userPermissions = listOf(Permission.MANAGE_SERVER).toTypedArray()
        this.help = "Sets up the discord server to represent a guild in BDO"
        this.arguments = "<guild name (same way as in game)>"
    }

    override fun executeCommand(commandEvent: CommandEvent?) {
        val guildOpt = bdoGuildService.getByNameAndRegion(commandEvent!!.args, Region.NORTH_AMERICA)
        if (guildOpt.isEmpty) {
            commandEvent.replyWarning("Guild not found. Double check your guild name. Contact Kikkia if this issue persists. (Only NA Supported so far)")
            return
        }
        val bdoGuild = guildOpt.get()
        val guild = guildService.getById(commandEvent.guild.id)

        if (guild.bdoGuild != null) {
            commandEvent.replyWarning("That guild is currently setup on another discord server. To change discord servers, remove the link to the old server with the `unlinkguild` command. (Must be run by admin on the old guild)")
            return
        }

        guild.bdoGuild = bdoGuild
        guildService.setBdoGuild(guild, bdoGuild)
        commandEvent.replySuccess("Successfully linked, war and member tracking functionality is now enabled!")
    }
}