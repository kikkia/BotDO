package com.bot.listeners

import com.bot.service.MetricsService
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import org.springframework.stereotype.Component

@Component
class CommandListener(val metricsService: MetricsService) : CommandListener {

    override fun onCompletedCommand(event: CommandEvent?, command: Command?) {
        metricsService.markCommandSuccess(command!!.name)
        super.onCompletedCommand(event, command)
    }

    override fun onCommandException(event: CommandEvent?, command: Command?, throwable: Throwable?) {
        metricsService.markCommandFailure(command!!.name)
        super.onCommandException(event, command, throwable)
    }
}