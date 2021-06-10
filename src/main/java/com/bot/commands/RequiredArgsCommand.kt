package com.bot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import java.lang.Exception

abstract class RequiredArgsCommand : Command() {

    override fun execute(commandEvent: CommandEvent) {
        if (checkArgs(commandEvent)) {
            try {
                executeCommand(commandEvent)
            } catch (e: Exception) {
                commandEvent.replyError("Something went wrong executing the command. Please try again and if this issue " +
                        "continues please reach out for support.")
                e.printStackTrace()
            }
        }
    }

    private fun checkArgs(event: CommandEvent): Boolean {
        if (event.args.isBlank()) {
            event.replyWarning("Arguments are required for this command: " + arguments)
            return false
        }
        return true
    }

    protected abstract fun executeCommand(commandEvent: CommandEvent?)
}