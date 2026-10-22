package com.bot.commands.user

import com.bot.db.entities.UserEntity
import com.bot.listeners.CommandListener
import com.bot.models.Region
import com.bot.service.UserService
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class SetDefaultRegionCommand(private val userService: UserService,
                              private val commandListener: CommandListener) : SlashCommand() {

    init {
        name = "defaultregion"
        help = "Set your default region for guild-history lookups"
        arguments = "<region code>"
        guildOnly = false
        botPermissions = emptyArray<Permission>()
        options = listOf(
            OptionData(OptionType.STRING, "region", "Your default BDO region", true)
                .addChoices(Region.values().map { Choice(it.code, it.code) })
        )
    }


    override fun execute(event: SlashCommandEvent) {
        event.deferReply(true).queue { hook ->
            try {
                val region = Region.getByCode(event.getOption("region")!!.asString)
                if (region == null) {
                    hook.editOriginal("Unknown region. Choose one of the regions offered by `/defaultregion`.").queue()
                    return@queue
                }

                saveRegion(event.user, region)
                hook.editOriginal("Your default lookup region is now ${region.code}.").queue()
            } catch (exception: Exception) {
                commandListener.onSlashCommandException(event, this, exception)
            }
        }
    }

    override fun execute(event: CommandEvent) {
        val region = Region.getByCode(event.args.trim())
        if (region == null) {
            event.replyWarning("Unknown region. Choose one of the regions offered by `/defaultregion`.")
            return
        }
        saveRegion(event.author, region)
        event.replySuccess("Your default lookup region is now ${region.code}.")
    }

    private fun saveRegion(discordUser: User, region: Region) {
        val user = userService.getById(discordUser.id) ?: UserEntity.from(discordUser)
        user.defaultRegion = region.code
        userService.save(user)
    }
}
