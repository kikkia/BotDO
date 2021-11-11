package com.bot.commands.user

import com.bot.commands.RequiredArgsCommand
import com.bot.models.Region
import com.bot.service.FamilyService
import com.bot.service.UserService
import com.bot.utils.GuildScrapeUtils
import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@Component
class WhoIsCommand(private val familyService: FamilyService,
                   private val userService: UserService) : RequiredArgsCommand() {

    init {
        name = "whois"
        help = "Gets info about a family in game, add region:(region code) for a region other than your default";
        arguments = "<family name> region:(region code, optional)"
    }

    override fun executeCommand(commandEvent: CommandEvent?) {
        val region = if (commandEvent!!.message.contentRaw.contains("region:")) {
            Region.getByCode(commandEvent.message.contentRaw.split("region:")[1].split(" ")[0])
        } else {
            Region.getByCode(userService.getById(commandEvent.member.user.id).defaultRegion)
        }
        if (region == null) {
            commandEvent.replyWarning("Invalid region supplied, valid regions are: ${Region.values()}")
            return
        }
        val familyOpt = familyService.getFamily(commandEvent.args, region, true)
        if (familyOpt.isEmpty) {
            // Try to search the site for a user, rather than use our cache
            if (familyOpt.isEmpty) {
                commandEvent.replyWarning("I do not have this user in my database. Please double check the family name. " +
                        "I only have information on users I see in guilds. I cannot get info if they have not been in a guild recently.")
                return
            }
        }
        val family = familyOpt.get()
        if (family.lastUpdated.toInstant().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
            // TODO: refresh user info
        }
        val guilds = family.memberships.stream().filter {!it.active}.map { it.guild.name }.collect(Collectors.toList());
        val activeGuild = family.memberships.stream().filter { it.active }.map { it.guild.name }.findFirst().orElseGet{ "None" }
        val pastGuilds = if (guilds.isEmpty()) "None that I know of (Past guilds indexed from 4/6/21)" else guilds.toString()
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle(family.name)
        embedBuilder.addField("Current Guild", activeGuild, true)
        embedBuilder.addField("Past Guilds", pastGuilds, false)
        commandEvent.reply(embedBuilder.build())
    }
}