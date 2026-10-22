package com.bot.listeners

import com.bot.service.MetricsService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jdautilities.command.CommandListener as JdaCommandListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CommandListener(val metricsService: MetricsService) : JdaCommandListener {

    private val log = LoggerFactory.getLogger(CommandListener::class.java)

    override fun onCompletedCommand(event: CommandEvent, command: Command) {
        metricsService.markCommandSuccess(command.name)
    }

    override fun onCommandException(event: CommandEvent, command: Command, throwable: Throwable) {
        metricsService.markCommandFailure(command.name)
        log.error("Failed to execute prefix command {}", command.name, throwable)
        event.replyError("Something went wrong running this command. Please try again, or DM kikkia if it keeps happening.")
    }

    override fun onCompletedSlashCommand(event: SlashCommandEvent, command: SlashCommand) {
        metricsService.markCommandSuccess(command.name)
    }

    override fun onSlashCommandException(event: SlashCommandEvent, command: SlashCommand, throwable: Throwable) {
        metricsService.markCommandFailure(command.name)
        log.error("Failed to execute slash command {}", command.name, throwable)
        val message = "Something went wrong running this command. Please try again, or DM kikkia if it keeps happening."
        if (event.isAcknowledged) {
            event.hook.editOriginal(message).queue()
        } else {
            event.reply(message).setEphemeral(true).queue()
        }
    }
}
