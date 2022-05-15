package com.bot.commands

import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class LeaveServerCommand(val guildService: GuildService) : Command() {

    init {
        name = "test"
        help = "test help"
        ownerCommand = true
    }

    override fun execute(p0: CommandEvent?) {
        for (id in p0!!.args.split(", ")) {
            val guildEntity = guildService.getById(id)
            if (guildEntity == null) {
                p0.replyWarning("Did not find guild $id")
            }
            val guild = p0.jda.getGuildById(id)
            if (guild == null) {
                p0.replyWarning("Did not find guild in discord $id")
                continue
            }
            guild.leave().complete()
        }
        p0.reactSuccess()
    }

}