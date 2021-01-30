package com.bot.commands

import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class TestCommand(val guildService: GuildService) : Command() {

    init {
        name = "test"
        help = "test help"
    }

    override fun execute(p0: CommandEvent?) {
        guildService.addFreshGuild(p0!!.guild)
        p0.reactSuccess()
    }

}