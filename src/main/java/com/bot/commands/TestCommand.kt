package com.bot.commands

import com.bot.db.entities.Guild
import com.bot.db.mapper.UserMapper
import com.bot.db.mapper.UserMapper.Companion.map
import com.bot.db.repositories.GuildRepository
import com.bot.db.repositories.ScrollInventoryRepository
import com.bot.db.repositories.TextChannelRepository
import com.bot.db.repositories.UserRepository
import com.bot.service.GuildService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.util.stream.Collectors

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