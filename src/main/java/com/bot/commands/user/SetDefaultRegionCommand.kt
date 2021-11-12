package com.bot.commands.user

import com.bot.commands.RequiredArgsCommand
import com.bot.models.Region
import com.bot.service.UserService
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class SetDefaultRegionCommand(private val userService: UserService) : RequiredArgsCommand() {

    init {
        name = "defaultRegion"
        help = "Sets your default region for looking up users and guilds";
        arguments = "<region code>"
    }


    override fun executeCommand(commandEvent: CommandEvent?) {
        val user = userService.getById(commandEvent!!.member.user.id)
        val region = Region.getByCode(commandEvent.args)
        if (region == null) {
            commandEvent.replyWarning("Region not found, please use one of the valid region codes: ${
                Region.values().map { it.code }.toList().joinToString(",")
            }")
            return
        }

        user.defaultRegion = region.code
        userService.save(user)
        commandEvent.reactSuccess()
    }
}