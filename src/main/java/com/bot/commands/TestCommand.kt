package com.bot.commands

import com.bot.db.entities.Guild
import com.bot.db.mapper.UserMapper
import com.bot.db.mapper.UserMapper.Companion.map
import com.bot.db.repositories.GuildRepository
import com.bot.db.repositories.ScrollInventoryRepository
import com.bot.db.repositories.TextChannelRepository
import com.bot.db.repositories.UserRepository
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.util.stream.Collectors

@Component
class TestCommand(val guildRepository: GuildRepository,
                  val scrollInventoryRepository: ScrollInventoryRepository,
                  val textChannelRepository: TextChannelRepository,
                  val userRepository: UserRepository) : Command() {

    init {
        name = "test"
        help = "test help"
    }

    override fun execute(p0: CommandEvent?) {
        p0!!.guild.members.forEach {
            userRepository.save(map(it))
        }
        guildRepository.save(Guild(p0.guild.id, p0.guild.name, p0.guild.members.stream().map { map(it) }.collect(Collectors.toSet())))
    }

}