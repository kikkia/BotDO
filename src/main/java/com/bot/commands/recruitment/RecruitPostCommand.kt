package com.bot.commands.recruitment

import com.bot.commands.RequiredArgsCommand
import com.bot.service.RecruitmentPostService
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.util.Arrays.asList

@Component
class RecruitPostCommand(val recruitmentPostService: RecruitmentPostService) : RequiredArgsCommand() {

    init {
        name = "recruitpost"
        help = "Mark that you posted a recruit message in a channel or world chat."
        arguments = "Server name or world"
    }

    val possibleVals = asList("kamasylvia", "olvia", "world", "discord", "reddit",
            "serendia", "balenos", "valencia", "mediah", "velia", "calpheon")

    override fun executeCommand(event: CommandEvent?) {
        val arg = event!!.args.split(" ")[0].toLowerCase()
        if (!possibleVals.contains(arg)) {
            event.replyWarning("Please use a valid server name. (No channel number)")
            return
        }

        recruitmentPostService.add(event.author, event.guild, event.args.toLowerCase())
        event.reactSuccess()
    }

}