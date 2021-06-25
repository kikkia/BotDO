package com.bot.commands.recruitment

import com.bot.service.GuildService
import com.bot.service.RecruitBellService
import com.bot.utils.FormattingUtils
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class BellCommand(private val recruitBellService: RecruitBellService, val guildService: GuildService) : Command() {

    init {
        this.name = "bell"
        this.help = "Marks that the guild recruitment notice (bell icon) was refreshed"
    }

    override fun execute(event: CommandEvent?) {
        val guild = guildService.getById(event!!.guild.id)
        if (recruitBellService.isOnCooldown(guild)) {
            val bell = recruitBellService.getLastByGuild(guild).get()
            event.replyWarning("${bell.author.getEffectiveName()} already refreshed the bell " +
                    "${FormattingUtils.prettyPrintMillis(
                            Instant.now().minusSeconds(bell.created!!.toInstant().epochSecond).toEpochMilli())}ago.")
        } else {
            recruitBellService.add(event.author, event.guild)
            event.reactSuccess()
        }
    }
}