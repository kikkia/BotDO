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
        commandEvent!!.channel.sendTyping().queue()
        val region = Region.getByCode(userService.getById(commandEvent.member.user.id).defaultRegion)
        val familyOpt = familyService.getFamily(commandEvent.args, region, true)
        if (familyOpt.isEmpty) {
            // Try to search the site for a user, rather than use our cache
            if (familyOpt.isEmpty) {
                commandEvent.replyWarning("I do not have this user in my database. Please double check the family name. " +
                        "I only have information on users I see in guilds. I cannot get info if they have not been in a guild recently.")
                return
            }
        }
        var family = familyOpt.get()
        var missingFromSite = false
        if (family.lastUpdated.toInstant().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
            val opt = familyService.syncSingleFromSite(family.name, region)
            missingFromSite = opt.isEmpty

            if (opt.isPresent) {
                family = opt.get()
            }
        }
        val guilds = family.memberships.stream().filter {!it.active}.map { it.guild.name }.collect(Collectors.toList());
        val activeGuildOpt = family.memberships.stream().filter { it.active }.map { it.guild.name }.findFirst()
        val activeGuild = if (activeGuildOpt.isEmpty && family.private) "Private" else activeGuildOpt.orElseGet{ "None" }
        val pastGuilds = if (guilds.isEmpty()) "None that I know of." else guilds.toString()
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle(family.name)
        if (missingFromSite) {
            embedBuilder.setDescription("This user was not found on the BDO website. Either the BDO site is down, or this family renamed or was banned.")
        }
        embedBuilder.addField("Current Guild", activeGuild, true)
        embedBuilder.addField("Past Guilds", pastGuilds, false)
        embedBuilder.setFooter("Guild history only indexed since ${region!!.indexDate}")
        commandEvent.reply(embedBuilder.build())
    }
}