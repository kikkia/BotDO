package com.bot.commands.user

import com.bot.listeners.CommandListener
import com.bot.models.Region
import com.bot.service.FamilyService
import com.bot.service.UserService
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class WhoIsCommand(private val familyService: FamilyService,
                   private val userService: UserService,
                   private val commandListener: CommandListener) : SlashCommand() {

    init {
        name = "whois"
        help = "Look up a BDO family's current guild and guild history"
        arguments = "<family name>"
        guildOnly = false
        botPermissions = emptyArray<Permission>()
        options = listOf(
            OptionData(OptionType.STRING, "family", "BDO family name", true),
            OptionData(OptionType.STRING, "region", "Override your default region for this lookup")
                .addChoices(Region.values().map { Choice(it.code, it.code) })
        )
    }

    override fun execute(event: SlashCommandEvent) {
        // Defer before database/network work, then run it on JDA's callback pool.
        event.deferReply().queue { hook ->
            try {
                val familyName = event.getOption("family")!!.asString.trim()
                if (familyName.isBlank()) {
                    hook.editOriginal("Please provide a BDO family name.").queue()
                    return@queue
                }

                val regionCode = event.getOption("region")?.asString
                    ?: userService.getById(event.user.id)?.defaultRegion
                    ?: Region.NORTH_AMERICA.code
                val region = Region.getByCode(regionCode)
                if (region == null) {
                    hook.editOriginal("Unknown region. Set a valid region with `/defaultregion`.").queue()
                    return@queue
                }

                val embed = lookup(familyName, region)
                if (embed == null) {
                    hook.editOriginal(notFoundMessage(region)).queue()
                } else {
                    hook.editOriginalEmbeds(embed).queue()
                }
            } catch (exception: Exception) {
                commandListener.onSlashCommandException(event, this, exception)
            }
        }
    }

    override fun execute(event: CommandEvent) {
        val familyName = event.args.trim()
        if (familyName.isBlank()) {
            event.replyWarning("Please provide a BDO family name.")
            return
        }
        val regionCode = userService.getById(event.author.id)?.defaultRegion ?: Region.NORTH_AMERICA.code
        val region = Region.getByCode(regionCode)
        if (region == null) {
            event.replyWarning("Unknown region. Set a valid region with `/defaultregion`.")
            return
        }
        val embed = lookup(familyName, region)
        if (embed == null) {
            event.replyWarning(notFoundMessage(region))
        } else {
            event.reply(embed)
        }
    }

    private fun notFoundMessage(region: Region): String {
        return "I couldn't find that family in ${region.code}. Check the family name and region. " +
            "Guild history is only available for families indexed by the bot."
    }

    private fun lookup(familyName: String, region: Region): MessageEmbed? {
        val family = familyService.getFamily(familyName, region, true).orElse(null) ?: return null
        val activeGuild = family.memberships.firstOrNull { it.active }?.guild?.name
            ?: if (family.private) "Private" else "None"
        val pastGuilds = family.memberships.filter { !it.active }
            .map { it.guild.name }.distinct().joinToString(", ").ifEmpty { "None that I know of." }
        val displayedPastGuilds = if (pastGuilds.length > MessageEmbed.VALUE_MAX_LENGTH) {
            pastGuilds.take(MessageEmbed.VALUE_MAX_LENGTH - 3) + "..."
        } else {
            pastGuilds
        }
        val embedBuilder = EmbedBuilder()
            .setTitle(family.name)
            .addField("Current Guild", activeGuild, true)
            .addField("Past Guilds", displayedPastGuilds, false)
            .setFooter("Guild history only indexed since ${region.indexDate} (${region.code})")
        if (family.lastUpdated.toInstant().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
            embedBuilder.setDescription("This family's cached history could not be refreshed from the BDO website.")
        }
        return embedBuilder.build()
    }
}
