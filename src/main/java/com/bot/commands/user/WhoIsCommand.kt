package com.bot.commands.user

import com.bot.commands.RequiredArgsCommand
import com.bot.models.Region
import com.bot.service.FamilyService
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@Component
class WhoIsCommand(private val familyService: FamilyService) : RequiredArgsCommand() {

    init {
        name = "whois"
        help = "Gets info about a family in game (Only NA atm)";
        arguments = "<family name>"
    }

    override fun executeCommand(commandEvent: CommandEvent?) {
        val familyOpt = familyService.getFamily(commandEvent!!.args, Region.NORTH_AMERICA)
        if (familyOpt.isEmpty) {
            commandEvent.replyWarning("I do not have this user in my database. Please double check the family name. " +
                    "I only have information on users I see in guilds. I cannot get info if they have not been in a guild recently.")
            return
        }
        val family = familyOpt.get()
        if (family.lastUpdated.toInstant().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
            // TODO: refresh user info
        }
        val guilds = family.memberships.stream().map { it.guild.name }.collect(Collectors.toList())
        val activeGuild = family.memberships.stream().filter { it.active }.map { it.guild.name }.findFirst().orElseGet{ "None" }
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle(family.name)
        embedBuilder.addField("Current Guild", activeGuild, true)
        embedBuilder.addField("Past Guilds", guilds.toString(), false)
        commandEvent.reply(embedBuilder.build())
    }
}