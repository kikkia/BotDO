package com.bot.commands.recruitment

import com.bot.service.GuildService
import com.bot.service.RecruitBellService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component

@Component
class BellCommand(private val recruitBellService: RecruitBellService, val guildService: GuildService) : Command() {

    init {
        this.name = "bell"
        this.help = "Marks that the guild recruitment notice (bell icon) was refreshed"
    }

    override fun execute(event: CommandEvent?) {
        val guild = guildService.getById(event!!.guild.id)
        if (recruitBellService.isOnCooldown(guild)) {
            event.replyWarning("Someone already refreshed the bell within the last 3 hours.")
        } else {
            recruitBellService.add(event.author, event.guild)
            event.reactSuccess()
        }
    }
}