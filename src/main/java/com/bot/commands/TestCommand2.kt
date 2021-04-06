package com.bot.commands

import com.bot.models.Region
import com.bot.utils.GuildScrapeUtils
import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class TestCommand2(val guildService: GuildService) : Command() {

    init {
        name = "test2"
        help = "test help"
    }

    override fun execute(p0: CommandEvent?) {
        p0!!.reply(GuildScrapeUtils.getGuildFamilies("CacklingCasket", Region.NORTH_AMERICA).stream().map { it.name }.collect(Collectors.toList()).toString())
    }

}